# ReadMe

## Introduction

This directory contains the source code for the [Eastern Idaho Photographic Society's](http://eips.net) website.
It includes fragments of websites stored in the content directory which are rendered using
[Perun](https://github.com/hashobject/perun),
[Boot](https://github.com/boot-clj/boot),
and [Selmer](https://github.com/yogthos/Selmer).

There are a few reasons for this technology stack

  1. Command line systems are easily integrated with third party tools such as text editors.

  2. Since the website is compiled before upload, only simple complete files are uploaded to the server.  This means that they can be hosted by nearly any service (including extremely cheep options like Amazon S3 or GitHub Pages), and because the hosting solution doesn't involve any complex processing, there is virtually no attack surface, you are unlikely to get hacked.

  3. Because everything is automated and documented (and links to tutorials are provided), it is easy to pick up, and immediately gain consistent behavior.  Every page will have consistent headers, because the automation ensures it.  This also makes it incredibly simple to add something to the header, as there is only one place to change to update everything.

      The benefits of consistent behavior can't be overstated.  For instance, this makes it simple to ensure that every image on the website has a copyright notice, or has been watermarked.  It also makes it possible to catch for mistakes before publishing. Spell check can be built into the publishing process, ensuring that everything is always spelled correctly on the publicly facing website.

  4. Broad tool support - Because most everything is based on simple text files, they can be processed using a wide variety of different tools, from highly specialized tools designed for specific purposes (for instance, to alphabetize a glossary), to generalized tools such as text editors.

  5. Plain text is eternal.  So long as you have this repository backed up

You also might want to look at the [glossary](./glossary.md) and the
[project glossary](./project_glossary.md)

## Hosting

The website is hosted on AWS S3 in the [eips.net](https://s3.console.aws.amazon.com/s3/buckets/eips.net/?region=us-east-1&tab=overview) S3 bucket.  There is also a test version hosted in the [test.eips.net](https://s3.console.aws.amazon.com/s3/buckets/test.eips.net/?region=us-east-1&tab=overview) S3 bucket.  The contents of these buckets are available on the web at the following links [eips.net](http://eips.net) and [test.eips.net](http://test.eips.net.s3-website-us-west-2.amazonaws.com).

## Use

For first time setup, see [below](#Initial_Setup).

The build is controlled by [build.boot](./build.boot) which includes the following tasks:

* `build` - Processes the source code and places the output in the `public` directory.
* `deploy-prod` - Executes build and publishes the results to the website.
* `deploy-test`- Executes build and publishes the results to the test environment.
* `dev` - Processes the source code and hosts it on a local web server.  It will also track built files and rebuild for any changes.  (Though it does not track changes to build.boot).    Press `Ctrl-C` to stop this.

These tasks can be executed from the root project directory with `boot {taskname}`.  For instance,you would use `boot dev` to start the dev watcher.  This will then print a http link (it will look like `Started Jetty on http://localhost:3000`) after which you can browse to [http://localhost:3000](http://localhost:3000) to see a local version of the website. (It may take a while to reach this point, and will print out reams of details about what it is doing.)  Similarly, `boot build` will get it to put the results in the build directory, aka 'public'.

### Making Changes

Changes to this project are managed using the [git](https://git-scm.com/) version control system. This means that there is a record of changes to this website and previous versions of the website can be checked out and viewed by anyone with a copy of the git repository.  This can be very useful when you realize that you have made a mistake, or wonder how something used to be done.

Once you are satisfied with a change, be sure to commit it to the repository.  First you will have to stage the changed files with `git add {list of changed files}`.  Then you can create a commit with `git commit`.  This will cause a text editor to open so that you can write a description and explanation of your changes.  This description is critical when trying to understand the history, so make sure to explain what is happening.

You may also want a better tutorial on Git.  For beginners I will recommend
[this](https://www.cloudways.com/blog/git-tutorial-for-beginners-version-control/).  You might also appreciate [this](https://www.udacity.com/course/how-to-use-git-and-github--ud775) and
[this](https://www.codecademy.com/learn/learn-git).  There is, of course, also the tradition of reading the [documentation](https://git-scm.com/documentation), which includes a [tutorial](https://git-scm.com/docs/gittutorial).

It would also be remiss of me to not mention some of the excellent graphical interfaces for git, such as, [SourceTree](https://www.sourcetreeapp.com/), [Github Desktop](https://desktop.github.com/), and [TortoiseGit](https://tortoisegit.org/).  You can find more on the [Git website](https://git-scm.com/downloads/guis/).

### Deployment

Once you are happy with the website as it appears on your system, you might want to deploy it.
First, you will need to get the credentials as described [below](#Setup-for-Deployment).

Boot will automatically deploy the website to the two mentioned S3 buckets.  Production is deployed with `boot deploy-prod` while the test environment is deployed with `boot deploy-test`.

## How It Works

Perun is a static site generator based on the boot build tool.  Perun contains tasks which are useful for generating a static HTML website.  We are using these tools, along with the Selmer templating engine to generate plain HTML files which are then uploaded to S3 by s3-sync.

We also have a small amount of custom logic used to generate the advancement pages, and error proof web fragments.  This logic is written in [Clojure](https://clojure.org/), and intentionally minimal.

The details for processing are described in the render-website task. sift means to copy files, so, sifting to-asset means that that file will appear on the website. [Markdown](https://daringfireball.net/projects/markdown/syntax) and HTML files will be processed, and rendered as described by site.core/page located in the src directory.  They are expected to contain [YAML](http://yaml.org/) front matter, much like that used [elsewhere](http://assemble.io/docs/YAML-front-matter.html).  This
YAML front matter is documented in site.core/known-yaml-keys.

### Directory Structure

The project has the following directory structure:

* content - contains the specific contents of the various web pages without any of the stuff the   templates add.
  * advancements - contains the full sized images and a manifest for each advancement project (in independent directories).  The advancement pages are generated automatically from these images and information in the manifest.

* js - Contains javascript files used for dynamic functionality on pages.
  Right now, this only includes the javascript which selects the sidebar image to display.

* index.html - This is the root page for the website.

* public - contains the finished renderings of the website after boot builds them.

* src - contains rendering instructions as well as the logic for generation of the Advancements pages

* target - would contain compiled clojure stuff.

* templates - contains the Selmer templates used to render various parts of the website.  Common,   shared content can be found in these templates.

* .gitignore - contains a list of files which should not be included in the source repository.   These files will not have their histories tracked and will not propagate from one computer to   another during git cloning.  Many of these files are incidental.

* build.boot - this controls the build process and defines the build tasks.  If a build task doesn't   work, look here first.

* aws-credentials.json - this file contains your AWS credentials.  Anyone who has it can deface the website.  Keep it secret; keep it safe; and certainly keep it out of Git.

## A Note on Markdown

You might have noticed that this is a Markdown file, and that this website supports Markdown.  I would suggest that you use it, after all, it is simple and it allows you to focus exclusively on the content.  However many people like the apparent simplicity of WYSIWYG (what you see is what you get) editors.  First, I will briefly observe that in something like Markdown, you always see what is going on.  There can't be any invisible formatting commands, because all formatting commands *must* be visible, after all, they are also just text.  Next, I will direct you to some tools to help out.

First, most modern text editors like [atom](https://atom.io/),
[scrivener](https://www.literatureandlatte.com/scrivener.php),
[emacs](https://www.gnu.org/software/emacs/) or [Sublime Text](https://www.sublimetext.com/) either support or have plugins that support markdown.  Many of these plugins even include a preview function (not that you really need tool support to edit markdown).  Also, most web authoring tools,
and platforms (such as WordPress) support Markdown as well.

Second, I will note that there are many excellent tools for markdown editing itself, including some WYSIWYGs.  Here is a nice [sampling](https://github.com/karthik/markdown_science/wiki/Tools-to-support-your-markdown-authoring).

## Initial Setup

In order to get this website working, you will first need to install git, and clone the repository.

```shell
git clone {repository url}
```

### Installing boot

Next, you may need to install boot.  Boot requires the Java JDK to work.  This can be gotten from the [Oracle Java website](https://java.com/en/download/) or from a "package manager":

* for mac: [Homebrew](https://github.com/homebrew/homebrew) - `brew update; brew cask install java`
* for Linux: [nix](http://nixos.org/nix) - [Instructions](https://blog.flyingcircus.io/2016/05/12/automatic-installation-of-oracle-java/)
* for Windows: [chocolatey](https://chocolatey.org/) - `choco install jdk8`

Next download Boot and place it somewhere in your path.  On Windows this can be done with:

```PowerShell
`choco install -y boot-clj`
```

On Mac or Linux, boot is available in (respectively):

* [Homebrew](https://github.com/homebrew/homebrew) - `brew install boot-clj`
* [nix](http://nixos.org/nix) - `nix-env -i boot`

### Finishing the installation

After Boot and Java are installed, Boot will install everything else needed to make everything work. Just execute one of the boot commands from the directory you cloned the EIPS website into (where the build.boot file is located)

```shell
boot build
```

### Setup for Deployment

For security reasons, the credentials needed to deploy the EIPS website are not included in this repository.  Before the deployment feature will work, you must place the correct credentials in `aws-credentials.yaml`.  The credentials are generated on the [AWS dashboard website](https://console.aws.amazon.com/iam/home?region=us-east-1#/security_credential) by clicking on the `Create New Access Key` button under `Access keys`.  You may need to click `Show Access Key` to actually see the information.

The contents of the `aws-credentials.yaml` file should look like the following (with the secrets filled in).

```yaml
access-key: <Access Key ID>
secret-key: <Secret Access Key>
```

This file should never *ever* be committed to Git.

# AWS information

### Account Number ()

Amazon Simple Storage Service (S3) [AWS Account: 225203654660]

### AWS sign-in page

<https://225203654660.signin.aws.amazon.com/console>

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
