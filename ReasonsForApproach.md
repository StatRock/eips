## Reasons for the Technology Approach

  1. Command line systems are easily integrated with third party tools such as text editors.

  2. Since the website is compiled before upload, only simple complete files are uploaded to the server.  This means that they can be hosted by nearly any service (including extremely cheap options like Amazon S3 or GitHub Pages).  Furthermore, because the hosting solution doesn't involve any complex processing, there is virtually no attack surface and thus you are unlikely to get hacked.

  3. Because everything is automated and documented (and links to tutorials are provided), it is easy to pick up and immediately gain consistent behavior.  Every page will have consistent headers, because the automation ensures it.  The approach makes it incredibly simple to add something to a header or footer, as there is only one place to change to update everything.

  4. Broad tool support - Because almost everything is based on simple text files, web site maintenance and changes can be processed using a wide variety of different tools, from highly specialized tools designed for specific purposes (for instance, to alphabetize a glossary), to generalized tools such as text editors.

  5. Plain text is eternal (as long as you have this repository backed up).

   The benefits of consistent behavior can't be overstated.  For instance, the webmaster can easily ensure that every image on the website has a copyright notice, or has been watermarked.  The approach also makes it possible to catch mistakes before publishing. Spell check can be built into the publishing process, ensuring that everything is always spelled correctly on the publicly facing website.

## Reasons for the Technology Approach--Cindy's Viewpoint

   I inherited a website from Roger Heng.  It was entirely HTML-based.
   My son, Marty Gentillon, mined the website and pulled out all of the content that was actually in use.  Then he did his programming magic to add templates that made it easy to apply a single header and footer to each of the pages.
   Marty reworked the archive critique pictures so that the display changes with the width of the screen from which it is being browsed.
   Marty added the further magic of making a picture on the right side change randomly.
   When he brought "Kickstart" into the mix, the look of the whole thing changed into something that I liked.
   In the meantime, I reworked the text in the website and reorganized it, while keeping all of Roger's basic content.  I set up a menu with sub-menus to help readers find content of interest.
   Eventually, I added some pages.
   Marty assisted greatly in the process of migrating the site to Amazon Web Services (AWS). Now the web hosting costs $11/year and web usage costs are about 50 cents/month.  The previous host had raised prices and was charging ~$64 every 6 months.  So the cost changed from ~$128/y to less than $20/y.
   The use of styles in Marty's approach is a strength.  Groups of items can be styled in one line instead of having to make changes in each applicable HTML file.

   There is some overhead in the way the current web site is a managed because of its use of 'git' 'repositories.'  I think the version control is a pain, but it does provide backup and it is a good tool for more than one person working on the website because any conflicting edits can be easily resolved.