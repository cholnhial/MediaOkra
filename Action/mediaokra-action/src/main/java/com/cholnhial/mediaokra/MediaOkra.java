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
import com.google.actions.api.response.helperintent.SignIn;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.actions_fulfillment.v2.model.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.ResourceBundle;

public class MediaOkra extends ActionsSdkApp {
    private static final String MEDIA_OKRA_CLIENT_ID = "763985932186-54djndf4nmv2bnic5lj7ks6e7pl229gt.apps.googleusercontent.com";

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
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        ResourceBundle rb = ResourceBundle.getBundle("resources",
                request.getLocale());

        LOGGER.info(request.getConversationData().toString());


        String said = request.getArgument("text").getTextValue();

        if (request.getUser().getIdToken() == null) {
            responseBuilder.add("I am unable to do anything without signing in. You can say \"sign in\" to get an account");
        } else {
            GoogleIdToken.Payload profile = getUserProfile(request.getUser().getIdToken());
            MediaOkraService mediaOkraService = new MediaOkraService(profile.getEmail());

             if (said.equalsIgnoreCase("generate code")) {
                try {

                    generateUserCodeInBackground(mediaOkraService);

                    responseBuilder.add("Your code is being generated, check back in a few minutes and say \"my code\".");
                } catch (Exception e) {
                    responseBuilder.add("sorry I can't generate your code, try again later: " + e.getMessage());
                }

            } else if (said.equalsIgnoreCase("my code")) {
                 String myCode = mediaOkraService.getUserCode();
                 responseBuilder.add("Your Media Okra code is: " + myCode);
                 responseBuilder.add("What would you like to do next?");
             }
             else if(said.equals("bye")) {
                responseBuilder.add("Good bye!");
                responseBuilder.endConversation();
             }
             else{

                try {
                    mediaOkraService.publishMediaCommand(said, mediaOkraService.getUserCode());
                    responseBuilder.add("I have sent a media command to your device");
                }
                catch (Exception e) {
                    responseBuilder.add("There was an issue sending the command to your device, please try again later.");
                }
            }
        }

        if (said.equalsIgnoreCase("sign in")) {
            responseBuilder.add(new SignIn().setContext("To get your account details")).build();
        }


        return responseBuilder.build();
    }

    private void generateUserCodeInBackground(MediaOkraService mediaOkraService) {
        new Thread(() -> {
            try {
                mediaOkraService.generateNewCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @ForIntent("com.cholnhial.mediaokra.MEDIA_COMMAND")
    public ActionResponse directMediaCommand(ActionRequest request) {
        ResponseBuilder responseBuilder = getResponseBuilder(request);

        LOGGER.info(request.getConversationData().toString());

        if(request.getUser().getIdToken() != null) {
            GoogleIdToken.Payload profile = getUserProfile(request.getUser().getIdToken());
            MediaOkraService mediaOkraService = new MediaOkraService(profile.getEmail());
            Argument arg = request.getArgument("command");
            try {
                mediaOkraService.publishMediaCommand(arg.getTextValue(), mediaOkraService.getUserCode());
                responseBuilder.add("I have sent a media command to your device");
            } catch (Exception e) {
                responseBuilder.add("There was an issue sending the command to your device, please try again later.");
            }
        } else {
            responseBuilder.add("I am unable to do anything without signing in. You can say \"sign-in\" to get an account");
        }


        return responseBuilder.build();
    }

    @ForIntent("actions.intent.SIGN_IN")
    public ActionResponse getSignInStatus(ActionRequest request) {
        ResponseBuilder responseBuilder = getResponseBuilder(request);
        if (request.isSignInGranted()) {
            GoogleIdToken.Payload profile = getUserProfile(request.getUser().getIdToken());
            MediaOkraService mediaOkraService = new MediaOkraService(profile.getEmail());

            responseBuilder.add(
                    "I got your account details, "
                            + profile.get("given_name"));
            generateUserCodeInBackground(mediaOkraService);
            responseBuilder.add("Your Media Okra code is being generated, to retrieve your code say: my code");
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
                        .setAudience(Collections.singletonList(MEDIA_OKRA_CLIENT_ID))
                        .build();
        GoogleIdToken idToken = verifier.verify(idTokenString);
        return idToken.getPayload();
    }


}
