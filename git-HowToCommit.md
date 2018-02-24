## Command Line Entries to Commit Changes

These commands allow one to commit the clump of files to the git repository (files except the ignore ones get saved)

```
git status        # Shows modified files
git add --all     # Says we will save all of them
git status        # Shows that the files are ready
git commit
```

The last command brings up a text file with space to fill in a title line, then there will be a blank line, then there is space for the user to give a description of the changes.  Complete the two needed items (do your own line wrapping on the description) and hit Enter.  Then the commit takes place.

```
git log         # view the changes.