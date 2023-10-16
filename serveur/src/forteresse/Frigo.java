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

public class Frigo {

	public final int LimiteFrigoX1;
	public final int LimiteFrigoX2;
	public final int LimiteFrigoY1;
	public final int LimiteFrigoY2;

	public Frigo(int FRIGO_X, int FRIGO_Y) {
		LimiteFrigoY1 = (FRIGO_Y / 10) - 1;
		LimiteFrigoY2 = LimiteFrigoY1 + 7;
		LimiteFrigoX1 = (FRIGO_X / 10) - 1;
		LimiteFrigoX2 = LimiteFrigoX1 + 7;
	}

	public boolean ToucheFrigo(int X, int Y) {
		return Y > LimiteFrigoY1 && Y < LimiteFrigoY2 && X > LimiteFrigoX1 && X < LimiteFrigoX2;
	}
}
