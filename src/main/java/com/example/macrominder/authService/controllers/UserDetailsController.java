package com.example.macrominder.authService.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserDetailsController {

    // perform validation of token first
    @GetMapping("/userDetails")
    public void userDetails(){

        // get all the userdetails:
        // email, name, accounts linked emails, profile picture URL

    }

    // perform validation of token first
    @PostMapping("/updateuserDetails")
    public void updateUserDetails(){

        // Receive image data as @RequestPart("profileImage") MultipartFile profileImage
        // Recieve other data as  @RequestParam("username") String username,

        // from frontend send formdata, containg:
        // email
        // username
        // googleAccesstoken
        // facebookaccesstoken
        // image
        // here we recieve image as multipart form data @Requestparam
        // First take the image and upload it to s3 and get back the URL
        // Now delete user profile image path and from s3 both
        // Now add this new image path
        // also for email check if it is already present anywhere other than this user in DB.
        // also if check if email is sent , password has been set in db , if no, dont return error as set password for email.
        // also if email sent is null or empty, remove password also, set it to null.
        // Now add or update rest of fields like username and email.
        // also on frontend make sure if email is changed, delete access and refresh tokens

        // for linked accounts:
        // get google accesstoken from above
        // validate google accesss token with api
        // if invalid throw error
        // if valid, extract email
        // check across all email,googleemail and facebookemail if already in use
        // except users all email, if match found return Email already in use.
        // if valid,
        // if oauth email empty add this to userinfo.googleemail
        // if oauth email not empty add this to userinfo.googleemail
        // if passed oauth value empty, set db value to null.

        // finally make sure atleast one email from link accounts or email is available for user.

        // finally send this new data, excluding image to notification service to send it to AWS SQS
        // to new email >> google email >> facebook email
    }


}
