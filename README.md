# MediaOkra
A simple Google Assistant Action to help me pause Udemy videos while I'm coding off the examples

## Projects

### Action
This folder contains the Google Assistant Action Project that is deployed to Google Cloud. 

### Client
This folder contains the application that listens for PUB/SUB messages from Google Cloud and invoke a media key requested. 

## Setup and running

### Step 1. 

#### Prerequisites

Do this first. You will need the Google Cloud stuff.

1. Download & install the [Google Cloud SDK](https://cloud.google.com/sdk/docs/)
1. [Gradle with App Engine Plugin](https://cloud.google.com/appengine/docs/flexible/java/using-gradle)
    + Run `gcloud auth application-default login` with your Gooogle account
    + Install and update the App Engine component,`gcloud components install app-engine-java`
    + Update other components, `gcloud components update`
1.  [Install the gactions CLI](https://developers.google.com/assistant/tools/gactions-cli)
    + You may need to grant execute permission, ‘chmod +x ./gactions’

### Configuring the Actions project

 Go to your [Google Cloud Projects](https://console.cloud.google.com/cloud-resource-manager).

There you look for the button **CREATE PROJECT**.

Give it a name such as mediaokra. 

Go to ```cd Action/mediaokra-action```

Then run ```gcloud init```

Making sure to authenticate with your Google Account if it's your first time. Associate the thingy with your app you created previously.

### Setting up ```action.json``` - coming soon
