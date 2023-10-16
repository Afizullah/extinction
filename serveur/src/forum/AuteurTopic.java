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

package forum;

public class AuteurTopic {
	private String Pseudo;
	private String Avatar;
	
	public AuteurTopic(String Pseudo, String Avatar) {
		this.Pseudo = Pseudo;
		this.Avatar = Avatar;
	}
	
	public boolean equals(String Pseudo) {
		return Pseudo.equalsIgnoreCase(Pseudo);
	}
	
	public String getAvatar() {
		return Avatar;
	}
}
