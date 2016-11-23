# LocationApp
###### Location Application for Agile Software Development
***


This mobile applications intention is to bring a new experience to users in a new social network. This social network is all about
connecting with others in the real world through technology. It allows users to see what events and activities are going on in their
local area and add their own activities as well. 

Users have access to a map through <b>Google Map API</b> and can see on a map what activities are currently going on.
Users will have access through a <b>SQLite database</b> to see a history of their activities that they have created.

Through the use of the GoogleSignOn API users will be able to sign on with their Google accounts and sync their data.
***

##App Theme:


<li>https://gist.github.com/PurpleBooth/109311bb0361f32d87a2</li>
<li>https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet</li>
<li><strong>Primary Color</strong>#8F1BB8</li>
<li><strong>Secondary Color</strong>#B563D2</li>
<li><strong>Tertiary Color</strong>#9F3CC2</li>
<li><strong>Accent Color</strong>#F5FD1C</li>


***


## About App:


The purpose of this app is to ~~be awesome!~~ find users *current* locations and add them to a list in a **SQLite** database. Users will sign on through the use of the [**GoogleSignOn API**](https://developers.google.com/identity/sign-in/android/)
***
```Java
// Configure sign-in to request the user's ID, email address, and basic profile. ID and
// basic profile are included in DEFAULT_SIGN_IN.
GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestEmail()
    .build();

// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
mGoogleApiClient = new GoogleApiClient.Builder(this)
    .enableAutoManage(this, this)
    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
    .build();
```
***

Markdown | Less | Pretty
--- | --- | ---
*Still* | `renders` | **nicely**
1 | 2 | 3
***
> Blockquotes are very handy in email to emulate reply text.
> This line is part of the same quote.







