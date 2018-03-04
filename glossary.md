# Glossary

Boot: A build tool written in Clojure

Build task: A step used in processing a build tool.  A single build task may resize all the images on the site, or might contain multiple other steps up to and including the entire build process

Build tool: A programming tool designed to produce output files from source files according to a designated repeatable process

Build: The basic high level process executed by the build tool.  This typically does not include deploying, as it is important to be able to see the potential results before deployment

Chocolatey: a package manager for Windows that facilitates the installation of other "packages" (eg. programs).

Clojure: A programming language belonging to the LISP (LISt Processing) family that runs on the JVM (Java Virtual Machine)

Command line: A REPL (see below) designed for computer interface.  For example, Windows Power Shell is a REPL tool.  The user enters commands and sees the results ("printed" on the screen) after execution.

Deployment: The process used to deliver a software system to the end users.  In this case, construction of the processed web pages, and sending them to a web server for hosting

Dev: development, the process of making changes to a software system before making them public. Typically done locally on a developer's computer.  (Used for the name of build tasks that are intended to optimize this process.)

Homebrew: a package manager for Apple PCs running the MacOs operating system

HTML: HyperText Markup Language, the markup language used by the web in general.  Your web browser reads and displays HTML files

HTTP: HyperText Transfer Protocol, the mechanism by which a web browser downloads a web page.

Markdown: A simple human markup language designed for easy human consumption.  Markdown files can be processed into a variety of other formats, including HTML.

Markup language: A type of programming language designed to allow commands to be embedded in arbitrary general text.  Typical commands control the display, layout, and structure of the general text

Nix: a package manger for linux

Package manager: A software system designed to reliably install other software systems based on package name.

Perun: A set of build tasks for boot designed to help build websites

REPL: Read Execute Print Loop, a tool which repeatedly reads commands written in a programming language, executes them, and then prints the results of the execution

Shell: see Command line.

Source control: see version control.

Static site: A website that is designed to run with minimal server side assistance.  Such a site typically includes pre-processed resources stored on a simple HTTP server

Version control: a system designed to maintain the history of a set of text documents, and perhaps other digital artifacts. Git is an example.
