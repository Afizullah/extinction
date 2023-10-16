/*
	Copyright 2020, Atelier801

    These files are part of Extinction Minijeux.

    Extinction Minijeux is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Extinction Minijeux is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Extinction Minijeux.  If not, see <https://www.gnu.org/licenses/>.

*/

package forteresse;

import java.util.ArrayList;
import java.util.Arrays;

public class Playlist {
	public static final int FACILE = 0;
	public static final int MOYEN = 1;
	// Réservé
	/*
	public static final int DIFFICLE = 2;
	public static final int TRES_DIFFICILE = 3;*/
	
	private static final Long PlaylistFacile[] = {1427017652385l, 1427017706900l, 1427017749435l, 1425886901340l, 1427017789320l, 1427017845309l, 1427025984390l, 1427027322420l, 1427490817763l, 
			1427493233021l, 1427968039125l, 1428003040764l, 1428003505919l, 1428003852471l, 1428004280584l, 1428004662540l, 1428004964447l, 1428005301107l, 
			1428005635608l, 1428006029288l, 1428147483091l, 1428152857697l, 1428939190754l, 1434629070469l, 1427492380621l, 1443874096208l, 1491662196216l, 
			1482250754955l};
	
	private static final Long PlaylistMoyen[] = {1425900984189l, 1425924144618l, 1425925471576l, 1426334670650l, 1426951565942l, 1426976375400l, 1427036003669l, 1427298622273l, 1427490213022l, 1427490261147l, 
			1427490295879l, 1427490348634l, 1427491071537l, 1427491144870l, 1427491678045l, 1427730816377l, 1428415177685l, 1428415237741l, 1428415413061l, 1428415584516l, 
			1428574819112l, 1428575849543l, 1428757605193l, 1428757894172l, 1428935065071l, 1428935770509l, 1428936418415l, 1428937816560l, 1428941606242l, 1428943611466l, 
			1428956711099l, 1429027051338l, 1429032765695l, 1429621786538l, 1430045572473l, 1430548091287l, 1430733946283l, 1430734175996l, 1430779321167l, 1431439525901l, 
			1431467575689l, 1431686139494l, 1431691619650l, 1432071642483l, 1433603053528l, 1433657012692l, 1434287508722l, 1434287592738l, 1434614725405l, 1435846704460l, 
			1437566961216l, 1450707946472l, 1450794656784l, 1450799683387l, 1452170977723l, 1452275412219l, 1454747343617l, 1455201357515l, 1462396433188l, 1465422557185l, 
			1466196231882l, 1466511137478l, 1466685720963l, 1472124349337l, 1472125980939l, 1472139945122l, 1482250819718l, 1481316357124l, 1495061799187l, 1473264313241l, 
			1432070860412l};
	
	private ArrayList<Long> playlist;
	private boolean boucler;
	private String idPlaylistOriginelle; // "O<N>" : officielle (ex : O0 = facile) ; "<N>" : non offi
	
	public Playlist() {
		playlist = null;
		boucler = true;
		idPlaylistOriginelle = "0";
	}
	
	public long getRandom() {
		if (playlist == null) {
			return 0l;
		}
		
		if (playlist.size() == 0) {
			if (!boucler) {
				return 0;
			} else {
				rechargerPlaylist();
			}
		}
		
		return playlist.remove((int)(Math.random() * playlist.size()));
	}
	
	public void setPlaylistOfficielle(int difficulte) {
		if (difficulte == Playlist.FACILE) {
			playlist = new ArrayList<Long>(Arrays.asList(PlaylistFacile));
			idPlaylistOriginelle = "O" + Playlist.FACILE;
		} else if (difficulte == Playlist.MOYEN) {
			playlist = new ArrayList<Long>(Arrays.asList(PlaylistMoyen));
			idPlaylistOriginelle = "O" + Playlist.MOYEN;
		}
	}
	
	private void rechargerPlaylist() {
		if (playlist == null || idPlaylistOriginelle.equals("0")) {
			return;
		}
		
		if (idPlaylistOriginelle.charAt(0) == 'O') { // Playlist officielle
			setPlaylistOfficielle(Integer.parseInt(idPlaylistOriginelle.substring(1)));
		} else { // Playlist non offi
			// TODO
		}
	}
	
	public static long getRandomOfficiel(int difficulte) {
		Long[] playlist;
		
		if (difficulte == Playlist.FACILE) {
			playlist = PlaylistFacile;
		} else if (difficulte == Playlist.MOYEN) {
			playlist = PlaylistMoyen;
		} else {
			playlist = new Long[]{0l};
		}
		
		return playlist[(int)(Math.random() * playlist.length)];
	}
}
