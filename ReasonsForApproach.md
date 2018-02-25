## Reasons for the Technology Approach

  1. Command line systems are easily integrated with third party tools such as text editors.

  2. Since the website is compiled before upload, only simple complete files are uploaded to the server.  This means that they can be hosted by nearly any service (including extremely cheep options like Amazon S3 or GitHub Pages).  Furthermore, because the hosting solution doesn't involve any complex processing, there is virtually no attack surface and thus you are unlikely to get hacked.

  3. Because everything is automated and documented (and links to tutorials are provided), it is easy to pick up and immediately gain consistent behavior.  Every page will have consistent headers, because the automation ensures it.  The approach makes it incredibly simple to add something to a header or footer, as there is only one place to change to update everything.

  4. Broad tool support - Because almost everything is based on simple text files, web site maintenance and changes can be processed using a wide variety of different tools, from highly specialized tools designed for specific purposes (for instance, to alphabetize a glossary), to generalized tools such as text editors.

  5. Plain text is eternal (as long as you have this repository backed up).

   The benefits of consistent behavior can't be overstated.  For instance, the webmaster can easily ensure that every image on the website has a copyright notice, or has been watermarked.  The approach also makes it possible to catch mistakes before publishing. Spell check can be built into the publishing process, ensuring that everything is always spelled correctly on the publicly facing website.