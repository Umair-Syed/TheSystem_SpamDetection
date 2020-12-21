<h1 align="center"> The System - Android Application</h1>
Team, <i>The Developers</i>'s project for Codathon event for the 6th CSI JK STATE STUDENT CONVENTION 2020.

<h3>Features:</h3>
<ol>
  <li>Login as Admin or User.</li>
  <li>OTP Authentication</li>
  <li>Add new product (only for admin). Admin can choose <i>Name, description and Image</i> for product</li>
  <li>View products</li>
  <li>Write a review (only for user).</li>
  <li>Delete review, block user (only for admin)</li>
  <li><b>Notification, if new spam review is detected</b> (only for admin)</li>
  <li>Remove Products</li>
</ol>

[<h3>Download APK</h3>](https://drive.google.com/file/d/1_ksjtQ8FPESf8OaGIM7EoAdhgnThvXyp/view)
<b>Note:</b> This app is not fully tested so unexpected behaviour may occur in some devices.

<h2>Spam detection</h2>
A sophisticated system is built to detect spam review. Detection process involves two phases
<ol>
  <li>Logically detecting if user has reviewed multiple times on same product</li>
  <li>Machine learning classification model (using Tensorflow lite) is used to analyze text and classify review into <i>truthful</i> or <i>deceptive</i>.
      Dataset is downloaded from Kaggle</li>
</ol>

[<h3>Check out Jupyter Notebook</h3>](classification.ipynb)
Tensorflow lite is used to embed model into Android app.


## Screens
Login             |  Write a review| Spam detection
:-------------------------:|:-------------------------: | :-------------------------:
![](/screenshots/sc1.jpg)  |  ![](/screenshots/sc6.jpg) | ![](/screenshots/sc9.jpg)

   
