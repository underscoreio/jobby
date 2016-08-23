# Jobby

[![Build Status](https://travis-ci.org/underscoreio/jobby.svg?branch=master)](https://travis-ci.org/underscoreio/jobby)

Automation of converting spreadsheet data into markdown for job opportunities.

> "Yes, Harry Potter!” said Dobby at once, his great eyes shining with excitement. “And if Dobby does it wrong, Dobby will throw himself off the topmost tower, Harry Potter!”

> “There won’t be any need for that,” said Harry hastily.”

An adaptation of the Google Sheets "quick start" example.

## Usage

You will need a `./src/main/resources/client_secret.json` as described in the [Google Quickstart Guide](https://developers.google.com/sheets/quickstart/java)

Then:

```
sbt 'runMain io.underscore.jobby.Main 1UN12o-LP3EFFw5VjeiP27om99_TmuFs3Pm9O1isinxY "Form Responses 1!A2:K"'
```

... for example.
