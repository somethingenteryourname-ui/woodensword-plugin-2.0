# WoodenSwordPlugin

A Paper/Spigot-compatible plugin for **Minecraft 1.21.11** (Java 21) that adds a
tracked "Kill Sword": a special Wooden Sword that gains Sharpness for every
player kill, up to Sharpness 35, and permanently bans its owner if it breaks.

## What it does

- `/woodensword` — OP-only. Gives a brand-new special Wooden Sword:
  `Kills: 0`, `Sharpness: 0`.
- `/woodensword <5|10|15|20|25|30|35>` — OP-only. Gives a brand-new special
  Wooden Sword that already has the chosen starting Sharpness level, with the
  kill counter still at `0`. Any other number shows an error listing the
  valid levels.
- Killing another **player** while holding the special sword increases its
  kill counter by 1 and its Sharpness by 5, capped at Sharpness 35. Killing
  mobs never affects it.
- The sword is identified only by a hidden, unique persistent-data tag (not
  by its name or lore), so a normal Wooden Sword can never be mistaken for
  it, and the data survives restarts, dropping/picking up, chests, and
  inventory moves.
- If the special sword actually breaks (reaches 0 durability), its owner is
  **permanently banned** from the server with the reason:
  `Your special wooden sword broke. You have been permanently banned.`

## Project layout

```
woodensword-plugin/
├── build.gradle.kts
├── settings.gradle.kts
├── src/main/java/com/example/woodensword/
│   ├── WoodenSwordPlugin.java     (plugin entry point)
│   ├── SwordKeys.java             (persistent-data keys)
│   ├── SwordManager.java          (create/identify/update the sword)
│   ├── WoodenSwordCommand.java    (/woodensword command)
│   ├── SwordKillListener.java     (Sharpness on player kills)
│   └── SwordBreakListener.java    (ban on sword break)
├── src/main/resources/plugin.yml
└── .github/workflows/build.yml    (builds the jar automatically)
```

## Building it yourself (optional — GitHub Actions does this for you)

You need JDK 21 installed. From the project root:

```
gradle shadowJar
```

(or `./gradlew shadowJar` if you generate a Gradle wrapper). The finished
plugin jar will be at:

```
build/libs/WoodenSwordPlugin-1.0.0.jar
```

That single jar is everything the server needs — there's nothing else to
install alongside it, since Paper's own API is provided by the server itself
at runtime and isn't bundled into the jar.

## Step-by-step: build with GitHub Actions and install on BisectHosting

1. **Create a new GitHub repository** (public or private, either works).
2. **Upload this entire project folder** to that repository — either by
   dragging all the files/folders into GitHub's web uploader, or with:
   ```
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPO.git
   git push -u origin main
   ```
   Make sure the `.github/workflows/build.yml` file is included — GitHub
   Actions only runs workflows it finds in that exact folder.
3. **Let GitHub Actions build it.** After pushing, open your repository on
   GitHub, click the **Actions** tab, and you should see a workflow run
   called "Build Plugin" already running (or about to start). Wait for it to
   finish with a green checkmark.
4. **Download the jar.** Click into that finished workflow run, scroll to
   the **Artifacts** section at the bottom, and download the
   `WoodenSwordPlugin` artifact. It will download as a `.zip` — unzip it to
   get the actual `WoodenSwordPlugin-1.0.0.jar` file.
5. **Open your BisectHosting server panel** and stop the server (or plan to
   restart it after).
6. **Open the File Manager** and navigate into the `plugins/` folder.
7. **Drag the `.jar` file** you unzipped into the `plugins/` folder.
8. **Restart the server** from the panel.
9. Once it's back online, join and run `/woodensword` (or e.g.
   `/woodensword 20`) as an OP.
10. The plugin works immediately — no other files or dependencies needed.

## Notes

- The command requires OP; non-OP players are shown a clear denial message.
- `/woodensword` with an invalid number (anything other than 5, 10, 15, 20,
  25, 30, or 35) shows a message listing the valid levels instead of giving
  an item.
- Running `/woodensword <level>` any number of times gives independent
  swords, each with its own separate kill count and Sharpness — one sword
  gaining a kill never affects another.
