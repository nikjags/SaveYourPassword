# Introduction
A pet-project of a simple console application that allow you to create and maintain your **own** password repository based on Google Drive.

# Main idea
To store your login-password pairs from everything requiring authentication.

# TL;DR How to get it?
All you need is:
- Java virtual machine version 11 or above (you can get it from JDK);
- [The application itself](https://drive.google.com/file/d/1eAs8HE3ZUy4pdN9aDPXqXIoQkD5P-s0h/view?usp=sharing);
- Google account.

Extract all files into your favorite folder.

# How to use it?
Click on **Save your password.bat**, stored in app folder, to run.

## Create your first password repository
After app starts, select "Create a new Google Drive password repository" by typing corresponding symbol 
(i don't know if i should teach github boys how to use a console app, but ok).

Next, you will find yourself in the main part of the program, where you can add new login-pass pairs, find or edit existing ones, and save changes into repository.

### What is this "tag" thing?
Tag field is used to represent an affiliation with something requiring login-password: sites, games, your office computer, etc.

You can write there everything you want; but remember: this field is used for login-pass pairs search, so write something what makes sense to future version of yours.

## Save your changes
While first save, application is managing a connection with GDrive, creates folder repository in there and uploads a password file to store.
It also creates a key file in application folder.

Any further connections with GDrive doesn't require authintication (see "What about credentials?" in FAQ down below).

### What should i do with the key file?
You should store it in flash drive or kind of place and keep away from children.

# FAQ

## What about credentials?
After first GDrive connection, application creates *bin/tokens* folder, where information about connection is stored. You don't need to make auntification again until you have *StoredCredentials* file in app folder.

But i do recommend you to delete *tokens* folder if you want to store the app and use it from time to time: time authintication is limited, so one day you have to manage a new connection.

## Is my data *truly* secure?
App encrypts data using [cryptographically strong random number generator](https://docs.oracle.com/javase/8/docs/api/java/security/SecureRandom.html) provided by Java. But you  always should keep in mind that RNG generates *pseudorandom* values, so it's hypothetically may be cracked (if someone get a password file). Thus i definetly not recommend to store pentagon passwords. But you wouldn't do it, right?

## Can i wait to some new versions and improvements?
Might be. First of all, i want to remake it into GUI app. Some sunny day it will be.

# And always remember!
It's only a pet-project done for one reason: i want to become a good programmer one day.

