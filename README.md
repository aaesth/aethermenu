# aethermenu
paper plugin to hide players and show a menu for hub servers  
im also making this to learn java so my code is ass

to reload the config do `/aethermenu reload`

<details>
  <summary>config example</summary>

```yaml
menu:
  size: 27
  freeze-player: true
  hide-players: true
  root-title: "§8Menu §7» §fMain"

folders:
  survival:
    title: "§aSurvival"
    slot: 11
    icon: GRASS_BLOCK

  pvp:
    title: "§cPvP"
    slot: 15
    icon: DIAMOND_SWORD

servers:
  survival-1:
    folder: survival
    slot: 11
    name: "§aSurvival #1"
    material: IRON_SWORD
    target: survival1

  survival-2:
    folder: survival
    slot: 13
    name: "§aSurvival #2"
    material: DIAMOND_SWORD
    target: survival2

  kitpvp:
    folder: pvp
    slot: 13
    name: "§cKitPvP"
    material: NETHERITE_SWORD
    target: kitpvp
```
</details>

<img src=http://cdn.aesth.cc/img/javaw_whTBjkIvIk.png>
