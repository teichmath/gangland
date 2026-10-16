# Archived: the female version of the game

The game used to ask "Male or Female?" at the start. A female game was the same game with women's names, a woman (Ardina) in place of Gregor, and pronouns and a few words swapped in all printed text. It was removed in October 2026 to keep a single version of the game; this file keeps the code that made it work, in case it's wanted again. The complete code is also in git history, e.g. commit `626b02f`.

Everything below is verbatim from `src/Gangland.java` as it was just before removal. Line numbers are from that version.

## How it worked

- `boolean malegame` (field on `Gangland`, passed to the constructor as `mgame`) was `false` for a female game.
- `fillNamesArray()` called `setNamesFemale()` instead of filling the male names.
- `masterStringOut()` rewrote every line of output with the substitutions below (he→she, Gregor→Ardina, fellow→chick, and so on). The game's own text was written for the male version; the female version existed only through these substitutions.
- Wherever the player typed a name, "ardina" was accepted or rejected in place of "gregor".

## The start-of-game prompt (in `main()`, line 507)

```java
			game.malegame = true;
			game.masterStringOut("* |                                                                | *");
			game.masterStringOut("* |                                                                | *");
			game.masterStringOut("* |     Male or Female?                                            | *");
		
			
			String mf_choice = "";
			do {
				game.masterStringOut("\n[ M / F ]");
				mf_choice = game.stringInput();
			} while (mf_choice.compareToIgnoreCase("m") != 0 && mf_choice.compareToIgnoreCase("f") != 0 && mf_choice.compareToIgnoreCase("male") != 0 && mf_choice.compareToIgnoreCase("female") != 0);
			if(mf_choice.compareToIgnoreCase("f") == 0 || mf_choice.compareToIgnoreCase("female") == 0) {
				game.malegame = false;
			}
```

## Women's names (line 4418)

```java
	public void setNamesFemale() {
		names[0] = "Jackie";
		names[1] = "Bernice";
		names[2] = "Eloise";
		names[3] = "Marge";
		names[4] = "Vera";
		names[5] = "Agnes";
		names[6] = "Bess"; 
		names[7] = "Chandra"; 
		names[8] = "Eulalie"; 
		names[9] = "Lucretia"; 
		names[10] = "Maude";
		names[11] = "Sigrid";
		names[12] = "Tilda";
		names[13] = "Rhona";
		names[14] = "Lucy";
		names[15] = "Kristanna";
		names[16] = "Sarah";
		names[17] = "Eliza";
		names[18] = "Amy";
		names[19] = "Diana";
		names[20] = "Penelope";
		names[21] = "Catherine";
		names[22] = "Helen";
		names[23] = "Laura";
		names[24] = "Alice";
		names[25] = "Maria";
		names[26] = "Malin";
		names[27] = "Agatha";
		names[28] = "Nadine";
		names[29] = "Lena";
		names[30] = "Erika";
		names[31] = "Wendy";
		names[32] = "Lorelei";
		names[33] = "Elrica";
		names[34] = "Katrina";
		names[34] = "Claudia";
		names[35] = "Isold";
		names[36] = "Odelia";
		names[37] = "Marcie";
		names[38] = "Winifred";
		names[39] = "Carmen";
		names[40] = "Sasha";
		names[41] = "Simone";
		names[42] = "Beatrix";
		names[43] = "Rhonwen";
		names[44] = "Ruby";
		names[45] = "Araminta";
		names[46] = "Jemima";
		names[47] = "Hester";
		names[48] = "Philomena";
		names[49] = "Gertrude";
			names[50] = "Rufstafaria";
			names[51] = "Hulota";
			names[52] = "Russokovia";
			names[53] = "Scrugsella";
			names[54] = "Daviena";
			names[55] = "Skippy";
			names[56] = "Paulina";
			names[57] = "Samantha";
			names[58] = "Grahamia";
			names[59] = "Clankesse";
			names[60] = "Houndbae";
			names[61] = "Casperina";
			names[62] = "Chasenia";
			names[63] = "Buddia";
			names[64] = "Gavriela";
			names[65] = "Davida";
			names[66] = "Philipina";
			names[67] = "Hinmania";
			names[68] = "Edwardina";
			names[69] = "Laverne";
			names[70] = "Ripley";
			names[71] = "Flynthia";
			names[72] = "Raina";
			names[73] = "Juliette";
			names[74] = "Willeini";
			names[75] = "Petunia";
			names[76] = "Larseene";
			names[77] = "Brunette";
			names[78] = "Andrea";
			names[79] = "Schrodengia";
			names[80] = "Gunthra";
			names[81] = "Eustisia";
			names[82] = "Geerta";
			names[83] = "Honzaaa";
			names[84] = "Vincenzia";
			names[85] = "Happia";
			names[86] = "Claxsonya";
			names[87] = "Georgina";
			names[88] = "Harrietta";
			names[89] = "Diskodja";
			names[90] = "Saltersina";
			names[91] = "Prattha";
			names[92] = "Jonie";
			names[93] = "Buberella";
			names[94] = "Einhardina";
			names[95] = "Gogolla";
			names[96] = "Bousquetina";
			names[97] = "Morleyanna";
			names[98] = "Richenna";
			names[99] = "Rudincia";
		
	}
```

## Text substitutions (in `masterStringOut()`, line 21434)

```java
		if(!malegame) {
			stringin = stringin.replaceAll("\\bhe\\b", "she");
			stringin = stringin.replaceAll("\\bHe\\b", "She");
			
			stringin = stringin.replaceAll("\\bhim\\b", "her");

			stringin = stringin.replaceAll("\\b'im\\b", "'er");

			stringin = stringin.replaceAll("\\bhis\\b", "her");
			stringin = stringin.replaceAll("\\bHis\\b", "Her");
			
			stringin = stringin.replaceAll("\\bhis,\\b", "hers,");
			stringin = stringin.replaceAll("\\bhis.\\b", "hers.");
			
			stringin = stringin.replaceAll("\\bhimself\\b", "herself");
			stringin = stringin.replaceAll("\\bHIMSELF\\b", "HERSELF");

			stringin = stringin.replaceAll("\\bman\\b", "woman");

			stringin = stringin.replaceAll("\\bmen\\b", "women");
			stringin = stringin.replaceAll("\\bMen\\b", "Women");

			stringin = stringin.replaceAll("\\bfellow\\b", "chick");
			stringin = stringin.replaceAll("\\bfellows\\b", "chicks");
			stringin = stringin.replaceAll("\\bgangfellow\\b", "gangchick");
			stringin = stringin.replaceAll("\\bgangfellows\\b", "gangchicks");
			stringin = stringin.replaceAll("\\blad\\b", "chick");
			stringin = stringin.replaceAll("\\blads\\b", "chicks");

			stringin = stringin.replaceAll("\\bbrother\\b", "sister");
			stringin = stringin.replaceAll("\\bbrothers\\b", "sisters");

			stringin = stringin.replaceAll("\\bGregor\\b", "Ardina");
			stringin = stringin.replaceAll("\\bGREGOR\\b", "ARDINA");
				
		}
```

## Other places that checked the version

- line 422: `game.malegame = true;`
- line 523: `game = new Gangland(boardside_answer, humansize_answer, extrazombiesize_answer, petsize_answer, game.malegame, main_high_scores, main_high_score_names_initials);`
- line 602: `if (player_name_answer.compareToIgnoreCase("gregor") == 0 && game.malegame) {`
- line 606: `if (player_name_answer.compareToIgnoreCase("ardina") == 0 && !game.malegame) {`
- line 747: `if(game.malegame) write_this += "   -- GREGOR";`
- line 748: `else write_this += "  -- ARDINA";`
- line 4041: `malegame = mgame;`
- line 4309: `//		System.out.println("malegame = "+malegame);`
- line 4311: `if(!malegame) setNamesFemale();`
- line 7389: `if((greet_choice.compareToIgnoreCase("gregor") == 0 && malegame) || (greet_choice.compareToIgnoreCase("ardina") == 0 && !malegame)) {`
- line 8573: `if((greet_choice.compareToIgnoreCase("gregor") == 0 && malegame) || (greet_choice.compareToIgnoreCase("ardina") == 0 && !malegame)) {`
- line 9914: `if ((pet_name_answer.compareToIgnoreCase("gregor") == 0 && malegame) || (pet_name_answer.compareToIgnoreCase("ardina") == 0 && !malegame)){`
- line 19328: `if((house_choice.compareToIgnoreCase("gregor") == 0 && malegame) || (house_choice.compareToIgnoreCase("ardina") == 0 && !malegame)) input_valid = true;`
- line 19371: `if(house_choice_chopped.length == 2 && ((house_choice_chopped[1].compareToIgnoreCase("gregor") == 0 && malegame) || (house_choice_chopped[1].compareToIgnoreCase("ardina") == 0 && !malegame))) {`
- line 19487: `if((house_choice.compareToIgnoreCase("gregor") == 0 && malegame) || (house_choice.compareToIgnoreCase("ardina") == 0 && !malegame)) {`
- line 19648: `if((gregor_choice_chopped[1].compareToIgnoreCase("gregor") == 0 && malegame) ||(gregor_choice_chopped[1].compareToIgnoreCase("ardina") == 0 && !malegame)) {`
- line 19694: `if(malegame) masterStringOut("Gregor scratches his bald pate and makes a face.");`
- line 19695: `else masterStringOut("Ardina crosses her arms, nonplussed...");`
