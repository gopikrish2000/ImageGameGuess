# ImageGameGuess

## Components Used/Done in this project

1. Used Latest Android SDK 25 ( which this app is currently targeting as well ) and all build tools etc using are updated ones.
2. Using Java8 with jackoptions which will enable Lambda expressions, method references etc.
3. Used RxJava, RxAndroid extensively to make UI responsive by pushing lot of code to Background and call backs in the Main thread with unsubscription in the onDestroy.
4. Using Retrofit to make network calls & using Glide as image library which provides caching options n image load failed etc.
5. Using SqlliteHelper for storing all finished games data.
6. Used MVP pattern in the code , to segregated UI part from the logic with Presenters.
7. Using PublishSubjects instead of interfaces for better code readability and handling of subscriptions.
8. Used RxBinding for binding view click events etc with throttleFirst , so that so avoid the multiple clicks
   /multiple events getting fired in a span of a second.
9. Using Espresso for Instrumentation test cases.

## Features included in this project

1. Added Offline game ( if network is not present or network call fails ) showing random images from the local. So internet is not mandatory for this app.
2. Normal game if some images are broken or erroring out , replacing those with random local images , so that game can continue using with help of Glide.
3. Added show all my games feature, will store all completed games data and show it.
4. If game is in between user can flip the mobile to landscape and still can play the game . Storing all data and handled use cases in onSavedInstanceState.
5. Written Instrumentation test cases to test the flow.
6. Created generic code with configuration (GameConfig.java). So upon changing the configuration game logic/UI changes.
7. Used extensively rxjava to delegate most of UI work to background thread ( like Databasecalls , some data processing work).
8. Put a timeout of 10 seconds to network call. So that game can be proceed to Offline mode without providing inconvience to user.
9. Added Play Again button , so user need not close the app to play the game again.
10. Added music ( music also composed by me using perfectpiano app ) for making game interesting.

## UI Features Added

1. Showing tick on top of image for conveying to user that is correct
2. Handled Ui for landscape mode as well.
3. Showing loader incase network response is slow.

## Screenshots

<img src="https://cloud.githubusercontent.com/assets/978945/24893462/69d0a946-1ea1-11e7-918b-a255b24d2dc6.png" width="350" height="600"> </img>
<img src="https://cloud.githubusercontent.com/assets/978945/24893467/7085e972-1ea1-11e7-9553-1f500794bd02.png" width="350" height="600"> </img>
<img src="https://cloud.githubusercontent.com/assets/978945/24893468/70c96364-1ea1-11e7-94fb-e0c86b4942bb.png" width="350" height="600"> </img>
<img src="https://cloud.githubusercontent.com/assets/978945/24893469/70cdf000-1ea1-11e7-8c46-e8fb335eca9e.png" width="350" height="600"> </img>
<img src="https://cloud.githubusercontent.com/assets/978945/24893471/70d2dcdc-1ea1-11e7-8ea3-18cbd6f2369e.png" width="350" height="600"> </img>
<img src="https://cloud.githubusercontent.com/assets/978945/24893470/70d06132-1ea1-11e7-83d8-9cd128b99f67.png" width="350" height="600"> </img>
