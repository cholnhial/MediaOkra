/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cholnhial.mediaokra;

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.ActionsSdkApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.actions.api.response.helperintent.SignIn;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.actions_fulfillment.v2.model.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Libraries for Google Assistant SDK

public class MediaOkra extends ActionsSdkApp {
  private static final String  MEDIA_OKRA_CLIENT_ID = "763985932186-54djndf4nmv2bnic5lj7ks6e7pl229gt.apps.googleusercontent.com";
  private static final Logger LOGGER = LoggerFactory
      .getLogger(MediaOkra.class);


  @ForIntent("actions.intent.MAIN")
  public ActionResponse welcome(ActionRequest request) {
    LOGGER.info("Welcome intent start.");
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    ResourceBundle rb = ResourceBundle.getBundle("resources",
        request.getLocale());
    responseBuilder.add(rb.getString("welcome"));

    LOGGER.info("Welcome intent end.");
    return responseBuilder.build();
  }

  @ForIntent("actions.intent.TEXT")
  public ActionResponse command(ActionRequest request) {
    LOGGER.info("Number intent start.");
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    ResourceBundle rb = ResourceBundle.getBundle("resources",
            request.getLocale());

    LOGGER.info(request.getConversationData().toString());


    String said = request.getArgument("text").getTextValue();

    if (said.equalsIgnoreCase("sign in")) {
      responseBuilder.add(new SignIn().setContext("To get your account details")).build();
    }
    else if(said.equalsIgnoreCase("generate code")) {
      responseBuilder.add("A new code has been generated for you, say send code to send the code to your email");
    }
    else if(said.equalsIgnoreCase("send code")) {
      responseBuilder.add("Your code has been sent to your mail");
    }
    else {
      if(request.getUser() == null) {
        responseBuilder.add("I am unable to do anything without signing in. You can say \"sign-in\" to get an account");
      } else {
        responseBuilder.add("You said: " + said);
      }
    }

    LOGGER.info("Number intent end.");
    return responseBuilder.build();
  }

  @ForIntent("com.cholnhial.mediaokra.PAUSE")
  public ActionResponse pause(ActionRequest request) {
    LOGGER.info("Number intent start.");
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    ResourceBundle rb = ResourceBundle.getBundle("resources");

    LOGGER.info(request.getConversationData().toString());

    GoogleIdToken.Payload profile = getUserProfile(request.getUser().getIdToken());
    Argument arg = request.getArgument("text");

    responseBuilder.add("Hello, " + profile.get("given_name") + ". You want to turn off " + arg != null ? arg.getTextValue() : "kitten );
    LOGGER.info("Number intent end.");
    return responseBuilder.build();
  }

  @ForIntent("actions.intent.SIGN_IN")
  public ActionResponse getSignInStatus(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (request.isSignInGranted()) {
      GoogleIdToken.Payload profile = getUserProfile(request.getUser().getIdToken());
      responseBuilder.add(
              "I got your account details, "
                      + profile.get("given_name")
                      + ". What do you want to do next?");
    } else {
      responseBuilder.add("I won't be able to save your data, but what do you want to do next?");
    }
    return responseBuilder.build();
  }
  private GoogleIdToken.Payload getUserProfile(String idToken) {
    GoogleIdToken.Payload profile = null;
    try {
      profile = decodeIdToken(idToken);
    } catch (Exception e) {
      LOGGER.error("error decoding idtoken");
      LOGGER.error(e.toString());
    }
    return profile;
  }

  private GoogleIdToken.Payload decodeIdToken(String idTokenString)
          throws GeneralSecurityException, IOException {
    HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
    JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    GoogleIdTokenVerifier verifier =
            new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    // Specify the CLIENT_ID of the app that accesses the backend:
                    .setAudience(Collections.singletonList(MEDIA_OKRA_CLIENT_ID))
                    .build();
    GoogleIdToken idToken = verifier.verify(idTokenString);
    return idToken.getPayload();
  }

}
