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

public class MondeForteresse {
	private String MapMondePerso = "";
	public String NumMonde;
	public final boolean DoubleAttaque;
	public final boolean MondePresident;
	public Frigo Frigo1;
	public Frigo Frigo2;
	
	public boolean MondePersonnalise = false;
	public String MondePersoStr;
	
	public MondeForteresse() {
		DoubleAttaque = false;
		MondePresident = false;
		
		Frigo1 = null;
		Frigo2 = null;
	}
	
	public MondeForteresse(boolean MondePresident) {
		this.DoubleAttaque = false;
		
		this.Frigo1 = null;
		this.Frigo2 = null;
		this.MondePresident = MondePresident;
	}

	public MondeForteresse(int FRIGO_X, int FRIGO_Y) {
		Frigo1 = new Frigo(FRIGO_X, FRIGO_Y);
		DoubleAttaque = false;
		MondePresident = false;
	}

	public MondeForteresse(int FRIGO1_X, int FRIGO1_Y, int FRIGO2_X, int FRIGO2_Y) {
		Frigo1 = new Frigo(FRIGO1_X, FRIGO1_Y);
		Frigo2 = new Frigo(FRIGO2_X, FRIGO2_Y);
		DoubleAttaque = true;
		MondePresident = false;
	}
	
	public void setMondePerso(String MONDEPERSOSTR) {
		NumMonde = "P";
		MondePersoStr = "P" + Forteresse.$ + MONDEPERSOSTR;
		MondePersonnalise = true;
	}
	
	public void setMapMondePerso(String Map) {
		MapMondePerso = Map;
	}
	
	public String getMapMondePerso() {
		return MapMondePerso;
	}
	
	public String toString() {
		if (MondePersonnalise) {
			return MondePersoStr;
		} else if (MondePresident) {
			return "7";
		} else {
			return NumMonde;
		}
	}
	
	public String getInfosMondePerso(String sep) {
		return MondePersoStr.substring(2).replace(Forteresse.$, sep); // Le "P-" n'est pas dedans
	}
	
	public static MondeForteresse CreerMondePerso(String Map, String[] InfosMondePerso) {
		// Monde personnalisé :
		ArrayList<Integer> PFrigos;
		ArrayList<Integer> ZRespawn = new ArrayList<Integer>();
		ArrayList<Integer> PRespawn = new ArrayList<Integer>();
		int NbFrigo;
		int Gravité;
		int Bords;
		int i = 0;
		int MAX;
		
		NbFrigo = Integer.parseInt(InfosMondePerso[0]) % 3;
		++i;
		
		if (NbFrigo == 0) {
			PFrigos = null;
		} else {
			PFrigos = new ArrayList<Integer>();
			PFrigos.add(Integer.parseInt(InfosMondePerso[1]));
			PFrigos.add(Integer.parseInt(InfosMondePerso[2]));
			
			if (NbFrigo == 2) {
				PFrigos.add(Integer.parseInt(InfosMondePerso[3]));
				PFrigos.add(Integer.parseInt(InfosMondePerso[4]));
			}
			i += PFrigos.size();
		}

		Gravité = Integer.parseInt(InfosMondePerso[i++]);

		// Zones de respawn
		MAX = i + 8;
		for(; i < MAX; ++i) {
			ZRespawn.add(((Integer.parseInt(InfosMondePerso[i]) * 10) / 10) % ((MAX - i) % 2 == 0 ? 200 : 100));
		}

		// Positions de respawn
		MAX = i + 4;
		for(; i < MAX; ++i) {
			PRespawn.add(Integer.parseInt(InfosMondePerso[i]) % ((MAX - i) % 2 == 0 ? 2000 : 1000));
		}
		
		Bords = Integer.parseInt(InfosMondePerso[i++]);
		
		return CreerMondePerso(Map, NbFrigo, PFrigos, Gravité, ZRespawn, PRespawn, Bords);
	}
	
	public static MondeForteresse CreerMondePerso(String Map, int NbFrigo, ArrayList<Integer> PFrigo, int Grav, ArrayList<Integer> ZRespawn, ArrayList<Integer> PRespawn, int Bords) {
		MondeForteresse monde;
		// Monde personnalisé
		StringBuilder MondePersoString =  new StringBuilder("" + NbFrigo);
		MondePersoString.append(Forteresse.$);
		if (NbFrigo != 0) {
			for (int i = 0; i < PFrigo.size(); ++i) {
				MondePersoString.append(PFrigo.get(i));
				MondePersoString.append(Forteresse.$);
			}
		}
		MondePersoString.append(Grav);
		MondePersoString.append(Forteresse.$);
		for (int i = 0; i < 8; ++i) {
			MondePersoString.append(ZRespawn.get(i));
			MondePersoString.append(Forteresse.$);
		}
		for (int i = 0; i < 4; ++i) {
			MondePersoString.append(PRespawn.get(i));
			MondePersoString.append(Forteresse.$);
		}
		
		MondePersoString.append(Bords);

		if (NbFrigo == 0) {
			monde = new MondeForteresse();
		} else if (NbFrigo == 1) {
			monde = new MondeForteresse(PFrigo.get(0), PFrigo.get(1));
		} else {
			monde = new MondeForteresse(PFrigo.get(0), PFrigo.get(1), PFrigo.get(2), PFrigo.get(3));
		}
		monde.setMondePerso(MondePersoString.toString());
		monde.setMapMondePerso(Map);
		
		return monde;
	}
}
