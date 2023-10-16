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

package serveur.stats;

public class Course extends Massacre {

	
	public String display() {
		return super.display() 
				+ "Arrivées en tête : " + ArriveePremier + "\n" 
				+ "Arrivées en vie : " + ArriveeEnVie+"\n";
	}
	
	public void toFlash() {
		super.toFlash();
		addField(ArriveePremier);
		addField(ArriveeEnVie);
	}
}


