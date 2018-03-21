# Overview of Web Site Process and Generation

(File:  "SimpleReadMe.md" 3/20/2018)

## Introduction

This directory contains the source code for the [Eastern Idaho Photographic Society's](http://eips.net) website.
The source code renders the web pages and photos stored in the "content" directory using
[Perun](https://github.com/hashobject/perun),
[Boot](https://github.com/boot-clj/boot),
and [Selmer](https://github.com/yogthos/Selmer).

There are additional separate files that contain further information, such as a [glossary.md](./glossary.md) and a [project_glossary.md](./project_glossary.md).

## Hosting

The website is hosted on Amazon Web Services (AWS) "S3." The service uses "buckets" to store the web content.  The main EIPS web site is stored in the "eips.net" S3 bucket.  The actual URL address of the eips.net bucket is

><https://s3.console.aws.amazon.com/s3/buckets/eips.net/?region=us-east-1&tab=overview.>

There is also a test version hosted in the "test.eips.net" S3 bucket, with URL

><https://s3.console.aws.amazon.com/s3/buckets/test.eips.net/?region=us-east-1&tab=overview>

The contents of these buckets are available on the web at the following links

* eips.net:  <http://eips.net>
* test.eips.net: <http://test.eips.net.s3-website-us-west-2.amazonaws.com>.

The test version of the website is not currently being used, but could be used among developers so that they could review changes and come to a consensus on them before deploying them to the main website.  Also it provides a method to show potential changes to groups of people for review.

## Use: Observe--Edit--Observe--Iterate--then Deploy

For first time setup, see "Initial Setup," below.

The website is created by a "build" process that transforms information in the "content" directory to create the specific files that need to be uploaded to the web site host.  The process is controlled by a program called "boot.exe."  The local file, "build.boot," provides instructions for the build.  The process includes the following tasks:

* `build` - Processes the source code and places the output in the `public` directory.
* `dev` - Processes the source code and hosts it on a local web server.  It will also track built files and rebuild when any changes are saved (except changes to "build.boot").  This program remains open and runs whenever changes are saved.  Press `Ctrl-C` to get out of this process.
* `serve` - Processes the source code and hosts it on a local web server, just like the 'dev' task.  However, it rebuilds only when it is re-executed.  It still remains open to maintain the local web server.  Press `Ctrl-C` to close it completely and drop the local web server.
* `deploy-prod` - Executes build and publishes the results to the website.
* `deploy-test`- Executes build and publishes the results to the test environment.

These tasks are executed from the root of the project directory (eips) by entering `boot {taskname}` in the command line (from the editor's terminal or from Windows Power Shell).  For instance, you would use `boot dev` to start the dev watcher.  This will then print a http link (it will look like `Started Jetty on http://localhost:3000`) after which you can browse to [http://localhost:3000](http://localhost:3000) to see a local version of the website. (It may take a minute or two to reach this point, and it will output many lines of details about what it is doing.) When you are satisfied with the local version of the website, you can run 'boot deploy-test' to publish it to the test or demonstration web site, or run 'boot deploy-prod' to publish it to http://www.eips.net.

### After Making Changes

Changes to this project are managed using the [git](https://git-scm.com/) version control system. This means that there is a record of changes to this website and previous versions of the website can be checked out and viewed by anyone with a copy of the git repository.  This backup feature is useful for undoing mistakes or observing how something used to be done.

All changes need to be "committed" to the git repository.  First, a change is "staged;" then it is "committed."  The commit process involves writing a short description/explanation of the changes. See the markdown file, git-HowToCommit.md for instructions on how to commit from the command line.  The process can also be executed from the text editor (see the additional documentation).

## How It Works

The "Boot" program, "boot.exe", processes the script, "build.boot." The script invokes "Perun," a static site generator.  Perun contains instructions for completing tasks such as "images-resize" and "render" which are useful for generating a static HTML website.  Website generation also uses the "Selmer" templating engine.  The output is plain HTML files which are then uploaded to S3 by a task called `s3-sync`.

Website generation also uses a small amount of custom logic.  This logic is written in a language called ["Clojure"](https://clojure.org/).  The logic particularly helps generate the advancement pages and the pictures that appear on the right side in most of the web pages.

The details for processing are described in the "render-website" task in the "build.boot" file. An example of the processing is a series of "Sift" calls. "Sift" is a task in boot.build that copies files.  The calls to "sift" describe files that are to be passed through directly from the "content" directory to the "public" directory where they will appear on the website. The remaining HTML files will be processed and rendered as described by the "page" function ("def") in the file, "site/core.clj" located in the "src" directory. The processing also involves the "Yet Another Markdown Language" (YAML"), which deals especially with "front matter" in the web pages. The "known-yaml-keys" function in "site/core.clj" allows the user to specify values for the "metadata keys" used for such things as page headers. The specific uses of the "front matter" are explained in the additional documentation.

### Directory Structure

The project has the following directory structure:

* content - contains the specific contents of the various web pages. Most of the HTML files have no header or footer information, because the 'basic_template' adds these. An important HTML file is index.html, which is the root or home page for the website. The directory also contains cascading style sheets (with extensions ending in ".css". Subdirectories include:
  * advancements - contains separate directories with images from monthly advancement session from 2007 to 2012.  A template creates an HTML file for each of these directories.
  * js - contains (1) the javascript file that randomly selects images for display from the list in 'basic_template.html_template' and (2)the 'Kickstart' javascript file.  'Kickstart' formatting provides the overall style and look of the website--see (http://www.99lime.com)
  * css - contains cascading style sheets from Kickstart.
  * public - contains the finished renderings of the website after boot builds them.
  * src - contains Clojure files with rendering instructions as well as the logic for generating the Advancements pages.  In the coding, "target" refers to the "public" directory.
  * templates - contains "Selmer" templates used to render various parts of the website.  Common, shared content (such as headers, menu, and footers) can be found in these templates.
  * .history and .vscode - automatically generated and maintained by visual code studio; not particularly useful to the web site developer.

### Additional Files in Root Directory

* .gitignore - contains a list of files which should not be included in the source repository.   These files will not have their histories tracked and will not propagate from one computer to another during git cloning.  Many of these files are incidental.

* build.boot - this controls the build process and defines the build tasks.  If a build task doesn't work, look here first.

* aws-credentials.yaml - this file contains your AWS credentials.  Anyone who has it can deface the website.  Keep it secret; keep it safe; and certainly keep it out of Git.

* several "markdown" files (with extension ".md") - contain documentation for this project.

## Initial Setup

First, in your browser, go to https://github.com/ and register.  You will supply a username and password.
Open the Windows Power Shell as an administrator.
To make the installations easier, install 'choco' from the web site, 'www.chocolatey.org'.
Next, install git, the version control tool: enter

'choco install -y git

Install the text editor, 'visualStudioCode' (VScode):

`choco install -y visualstudiocode`

Now you can access the files that make the web site. In your browser, go to

[the repository on github](https://github.com/StatRock/eips)
 
Click the button labeled 'fork.'  This will create a copy of the files under your GitHub username (https://github.com/<<<YourName>>>/eips.
Then, to get the files on your local PC, open the Windows Power Shell or open a terminal in VSCode and enter the following command:

git clone https://github.com/{your-github-username}/eips.git

{This command will place the local repository in a directory called 'eips.'}

### Installing boot

Open the Windows Power Shell as an administrator.
Install the Java Development Kit (JDK):

`choco install -y jdk8`

Now you are ready to install the program, boot.exe, that builds files in the 'public' directory.  Enter the following (one line):

wget -Uri https://github.com/boot-clj/boot-bin/releases/download/latest/boot.exe -Outfile $env:SystemRoot/boot.exe

### Finishing the installation

After Boot and Java are installed, Boot will install everything else needed to make everything work. Just execute one of the boot commands from the directory you cloned the EIPS website into (where the build.boot file is located)

```shell
boot build
```

### Setup for Deployment

For security reasons, the credentials needed to deploy the EIPS website are not included in this repository.  Before the deployment feature will work, you must place the correct credentials in a file called  `aws-credentials.yaml`.  The credentials are generated on the
[AWS dashboard website](https://console.aws.amazon.com/iam/home?region=us-east-1#/security_credential)

by clicking on the `Create New Access Key` button under `Access keys`.  You may need to click `Show Access Key` to actually see the information.

The contents of the `aws-credentials.yaml` file should look like the following (with the secrets filled in).

```yaml (Yet Another Mark-up Language)
access-key: <Access Key ID>
secret-key: <Secret Access Key>
```

This file should never *ever* be committed to Git.

## AWS information

### Account Number

Amazon Simple Storage Service (S3) [AWS Account: 225203654660] 

### AWS sign-in page

<https://225203654660.signin.aws.amazon.com/console>

### Name servers 

```
ns-345.awsdns-43.com.
ns-1945.awsdns-51.co.uk.
ns-667.awsdns-19.net.
ns-1518.awsdns-61.org.
```

### SOA (Start of Authority) name servers

```
ns-345.awsdns-43.com. 
awsdns-hostmaster.amazon.com. 1 7200 900 1209600 86400
```

### Useful links

* [EIPS web URL (direct Amazon URL)](http://eips.net.s3-website-us-west-2.amazonaws.com/index.html)

* [EIPS test web URL (direct Amazon URL)](http://test.eips.net.s3-website-us-west-2.amazonaws.com/index.html)

* [AWS Accounts Console](https://console.aws.amazon.com/iam/home?region=us-east-1#/home)

* [Route 53 Management Console at Amazon](https://console.aws.amazon.com/route53/home?region=us-east-2#resource-record-sets:ZB4GJ1E0ZA7VD)

* [S3 Storage (see directories--website content and logs)](https://s3.console.aws.amazon.com/s3/home)

* [CDN (Content Delivery Network), not currently in use](https://console.aws.amazon.com/cloudfront/home?region=us-east-1#)
