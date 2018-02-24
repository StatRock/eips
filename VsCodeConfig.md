# Configuring visual studio code

You can set your settings in visual studio code by pressing `Ctrl-Shift-P` and typing `preferences open user settings`.  This will open the settings view where you can see possible settings on the left and your configured settings on the right.  I recommend that you start by pasting the following on the right:

``` cson
{
    "terminal.integrated.scrollback": 10000,
    "editor.fontFamily": "Consolas, 'Lucida Console', 'Courier New', monospace",
    "editor.wordWrap": "bounded",
    "workbench.colorTheme": "PowerShell ISE",
    "editor.rulers": [
        100
    ],
    "editor.wordWrapColumn": 100,
    "workbench.panel.location": "bottom",
    "files.associations": {
        "build.boot": "clojure"
    },
    "[markdown]": {
        "editor.wordWrap": "bounded",
        "editor.quickSuggestions": false
    }
}
```

A couple of optional settings you might want to configure:

* If the markdown preview continually popping up is driving you crazy, add `"instantmarkdown.autoOpenBrowser": false,` to the beginning of the file.

Also, the first time you open this project, Visual Studio Code will recommend that you install a bunch of extensions.  You want them.