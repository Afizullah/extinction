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

package serveur;

public class Local {

	//public static final boolean LOCAL = false; /*// production
	public static final boolean LOCAL = true; // dévelopemment*/
	
	public static final boolean BETA = false; // Activer la bêta : LOCAL = false et BETA = true
	
	public static final String BDD_USER = "extinction";
	public static final String BDD_PASS = "extinction";
	public static final String BDD_NAME = "jdbc:mysql://localhost:3306/extinction";
	public static final String BDD_BETA_NAME = "jdbc:mysql://localhost:3306/beta";
	
	// @TODO
	public static final String BDD_USER_LOCAL = "extinction";//"user";
	public static final String BDD_PASS_LOCAL = "extinction";//"mdp";
	public static final boolean UtiliserSQLite = false;
	//public static final String BDD_NAME_LOCAL = "jdbc:sqlite:extinction.db";
	public static final String BDD_NAME_LOCAL = "jdbc:mysql://localhost:3306/extinction";
}
