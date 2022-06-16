<h1 align="center"> The System - Android Application</h1>
Team, <i>The Developers</i>'s project for Codathon event for the 6th CSI JK STATE STUDENT CONVENTION 2020.<br>
<b>Update: This Project was awarded the FIRST prize</b>

<h3>Features:</h3>
<ol>
  <li>Login as Admin or User.</li>
  <li>OTP Authentication</li>
  <li>Add new product (only for admin). Admin can choose <i>Name, description, price and Image</i> for product</li>
  <li>View products</li>
  <li>Write a review (only for user).</li>
  <li>Delete review, block/unblock user (only for admin)</li>
  <li><b>Notification, if new spam review is detected</b> (only for admin)</li>
  <li>Remove Products</li>
</ol>

[<h3>Download APK</h3>](https://drive.google.com/file/d/1upYLB8OaC1YOneYHoxV9WRbCFGbabEQi/view?usp=sharing)
<b>Note:</b> This app is not fully tested so unexpected behaviour may occur in some devices.

<h2>Spam detection</h2>
A sophisticated system is built to detect spam reviews. Detection process involves two phases
<ol>
  <li>Logically detecting if user has reviewed multiple times on the same product</li>
  <li>Machine learning classification model (using Tensorflow lite) is used to analyze text and classify review into <i>truthful</i> or <i>deceptive</i>.
      Dataset is downloaded from Kaggle</li>
</ol>
<blockquote>If a review is detected as spam in both the phases, then review is confirmed to be spam and <b>Alert Notification</b> is show to admin.
  If review is detected as spam in anyone of the phase, then review is marked as <i>potential spam</i> and admin is notified.</blockquote>


[<h3>Check out Jupyter Notebook</h3>](classification.ipynb)
Tensorflow lite is used to embed model into Android app.


### Phone numbers for testing:
Phone number          | Verification code
:-------------------------:|:-------------------------: 
+91 66666 66666  |  	666666
+91 11 1111 1111  |  	111111
+91 88888 88888  |  	888888


## Screenshots
Welcome Page           | Login via Phone 
:-------------------------:|:-------------------------: 
![](/screenshots/sc1.jpg)  |  ![](/screenshots/sc3.1.jpg)

Submit OTP           | Submit details Page
:-------------------------:|:-------------------------: 
![](/screenshots/sc3.2.jpg)  |  ![](/screenshots/sc2.jpg)


Home page (Notification for admin) | Add Product (admin feat.)
:-------------------------:|:-------------------------: 
![](/screenshots/sc9.jpg)  |  ![](/screenshots/sc4.jpg)


Write Review (user)          | Review Details (admin)
:-------------------------:|:-------------------------: 
![](/screenshots/sc6.jpg)  |  ![](/screenshots/sc7.jpg)


Navigation         | Reviews Page
:-------------------------:|:-------------------------: 
![](/screenshots/sc5.jpg)  |  ![](/screenshots/sc8.jpg)

When admin blocks user. User opens app, get this msg 
:-------------------------:
![](/screenshots/sc10.jpg)  

Note: This app was developed in hurry because of limited time in hacakathon. So bugs and issues are expected.
