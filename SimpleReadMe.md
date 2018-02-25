# Overview of Web Site Process and Generation

(File:  "SimpleReadMe.md" 2/22/2018)

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

## Use

For first time setup, see "Initial Setup," below.

The website is created by a "build" process that transforms information in the "content" directory to create the specific files that need to be uploaded to the web site host.  The process is controlled by a program called "boot.exe."  The local file, "build.boot," provides instructions for the build.  The process includes the following tasks:

  * `build` - Processes the source code and places the output in the `public` directory.
  * `deploy-prod` - Executes build and publishes the results to the website.
  * `deploy-test`- Executes build and publishes the results to the test environment.
  * `dev` - Processes the source code and hosts it on a local web server.  It will also track built files and rebuild for any changes.  (Though it does not track changes to build.boot).    This program remains open and runs whenever changes are saved.  Press `Ctrl-C` to get out of this process.

These tasks can be executed from the root project directory with `boot {taskname}`.  For instance,you would use `boot dev` to start the dev watcher.  This will then print a http link (it will look like `Started Jetty on http://localhost:3000`) after which you can browse to [http://localhost:3000](http://localhost:3000) to see a local version of the website. (It may take a while to reach this point, and it will output many lines of details about what it is doing.)  Similarly, `boot build` will put the results in the "build" directory, aka 'public'.

### Making Changes

Changes to this project are managed using the [git](https://git-scm.com/) version control system. This means that there is a record of changes to this website and previous versions of the website can be checked out and viewed by anyone with a copy of the git repository.  This backup feature is useful for undoing mistakes or observing how something used to be done.

All changes need to be "committed" to the git repository.  First, a change is "staged;" then it is "committed."  The commit process involves writing a short description/explanation of the changes.

### Deployment

Once you are happy with the website as it appears on your system, you might want to deploy it.
First, you will need to get the credentials as described [below](#Setup-for-Deployment).

"Boot" will automatically deploy the website to the two mentioned S3 buckets.  Production is deployed with `boot deploy-prod` while the test environment is deployed with `boot deploy-test`.

## How It Works

The "Boot" program, "boot.exe", processes the script, "build.boot." The script invokes "Perun," a static site generator.  Perun contains instructions for completing tasks such as "images-resize" and "render" which are useful for generating a static HTML website.  Website generation also uses the "Selmer" templating engine.  The output is plain HTML files which are then uploaded to S3 by a task called `s3-sync`.

Website generation also uses a small amount of custom logic.  This logic is written in a language called ["Clojure"](https://clojure.org/).  The logic particularly helps generate the advancement pages and the pictures that appear on the right side in most of the web pages.

The details for processing are described in the "render-website" task in the "build.boot" file. An example of the processing is a series of "Sift" calls. "Sift" is a task in boot.build that copies files.  The calls to "sift" describe files that are to be passed through directly from the "content" directory to the "public" directory where they will appear on the website. Markdown and HTML files will be processed and rendered as described by the "page" function ("def") in the file, "site/core.clj" located in the "src" directory. The processing also involves the "Yet Another Markdown Language" (YAML"), which deals especially with front matter in the web pages. The "known-yaml-keys" function in "site/core.clj" allows the user to specify values for the "metadata keys" used for such things as page headers.

### Directory Structure

The project has the following directory structure:

* content - contains the specific contents of the various web pages without any of the stuff the templates add.  Content includes html files and several directories.  An important html file is index.html, which is the root page for the website. The directory also contains cascading style sheets (with extensions ending in ".css". Subdirectories include:
  * advancements - contains the full sized images and a manifest for each advancement project (in independent directories).  The advancement pages are generated automatically from these images and information in the manifest.
  * js - contains javascript files used for dynamic functionality on pages.
  Right now, the only javascript routine selects the sidebar image to display.
  * css - contains cascading style sheets from ["HTML Kickstart"](http://www.99lime.com) that provide the general style and look of the web site.

* public - contains the finished renderings of the website after boot builds them.

* src - contains Clojure files with rendering instructions as well as the logic for generation of the Advancements pages.  In the coding, "target" refers to the compiled program output stored in the "public" directory.

* templates - contains "Selmer" templates used to render various parts of the website.  Common, shared content (such as headers, menu, and footers) can be found in these templates.

* .history and .vscode - automatically generated and maintained by visualcodestudio; not particularly useful to the web site developer.

### Additional Files in Root Directory

* .gitignore - contains a list of files which should not be included in the source repository.   These files will not have their histories tracked and will not propagate from one computer to another during git cloning.  Many of these files are incidental.

* build.boot - this controls the build process and defines the build tasks.  If a build task doesn't work, look here first.

* aws-credentials.yaml - this file contains your AWS credentials.  Anyone who has it can deface the website.  Keep it secret; keep it safe; and certainly keep it out of Git.

* several "markdown" files (with extension ".md") - contain documentation for this project.

## Initial Setup

In order to get this website working, you will first need to install git, then go to [the repository on github](https://github.com/StatRock/eips) fork the repository (by clicking "fork") on github, and clone it to your local machine.
```shell
git clone https://github.com/{your-github-username}/eips.git {optionally, path to put it, otherwise it will be in an eips directory}
```

### Installing boot

Next, the website maintenance requires "boot.exe". Boot requires the Java JDK to work.  This can be gotten from the 
[chocolatey](https://chocolatey.org/) - `choco install jdk8`

Next download Boot and place it somewhere in your path.  On Windows this can be done with:
```PowerShell
wget -Uri https://github.com/boot-clj/boot-bin/releases/download/latest/boot.exe -Outfile $env:SystemRoot/boot.exe
```

### Finishing the installation

After Boot and Java are installed, Boot will install everything else needed to make everything work. Just execute one of the boot commands from the directory you cloned the EIPS website into (where the build.boot file is located)

```shell
boot build
```

### Setup for Deployment

For security reasons, the credentials needed to deploy the EIPS website are not included in this repository.  Before the deployment feature will work, you must place the correct credentials in `aws-credentials.yaml`.  The credentials are generated on the 
[AWS dashboard website](https://console.aws.amazon.com/iam/home?region=us-east-1#/security_credential) 
by clicking on the `Create New Access Key` button under `Access keys`.  You may need to click `Show Access Key` to actually see the information.

The contents of the `aws-credentials.yaml` file should look like the following (with the secrets filled in).

```yaml
access-key: <Access Key ID>
secret-key: <Secret Access Key>
```

This file should never *ever* be committed to Git.

## AWS information

### Account Number ()

Amazon Simple Storage Service (S3) [AWS Account: 225203654660] 
 
### Name servers ()

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

* [EIPS test web URL (direct Amazon URL)](http://test.eips.net.s3-website-us-west-2.amazonaws.com/index.html)
* [EIPS web URL (direct Amazon URL)](http://eips.net.s3-website-us-west-2.amazonaws.com/index.html)
* [S3 Storage (see directories--website content and logs)](https://s3.console.aws.amazon.com/s3/home)
* [CDN (Content Delivery Network), not currently in use](https://console.aws.amazon.com/cloudfront/home?region=us-east-1#)
* [AWS Accounts Console](https://console.aws.amazon.com/iam/home?region=us-east-1#/home)
* [Route 53 Management Console at Amazon](https://console.aws.amazon.com/route53/home?region=us-east-2#resource-record-sets:ZB4GJ1E0ZA7VD)