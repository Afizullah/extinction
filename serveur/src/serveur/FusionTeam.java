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

import java.util.ArrayList;

public class FusionTeam {

	public ArrayList<Integer> Alliance;
	public int Id;

	public FusionTeam(int Id) {
		this.Id = Id;
		this.Alliance = new ArrayList<Integer>();
	}

	public boolean contains(Integer id)
	{
		return this.Alliance.contains(id);
	}
	
	public void add(Integer id)
	{
		this.Alliance.add(id);
	}

	public void remove(Integer id)
	{
		this.Alliance.remove(id);
	}
	
}