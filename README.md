# MediaOkra
A simple Google Assistant Action to help me pause Udemy videos while I'm coding off the examples.
After developing this project I learned that Google's Cloud Platform was actually quite intuitive lots of things work out of the box.
I was able to get Google Action SDK to talk with Google Pub/Sub. I needed Google Pub/Sub to pass messages from the Google Action to the clients running on desktop/laptop.
The developer experience was amazing. I look forward to using more of Google's Cloud Platform.

To play around with this Google Action go to the Official website for details: https://cholnhial.github.io/MediaOkra/#


## Projects

### Action
This folder contains the Google Assistant Action Project that is deployed to Google Cloud. 

### Client
This folder contains the application that listens for PUB/SUB messages from Google Cloud and invoke a media key requested. 


To learn more about setting up Actions visit this Google Aciton sample page for Java which I learned from https://github.com/actions-on-google/actionssdk-say-number-java
