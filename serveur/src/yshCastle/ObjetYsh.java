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

package yshCastle;

public class ObjetYsh {
	public static enum ObjetsDonjon {
		GemmeDestin(0), OrbeVision(1), SableDore(2), CharmeEternite(3), CoeurDemon(4), SacBombes(5),
		CookieMagique(6), BenedictionDieux(7), CommandeTroll(8), DagueSacrificielle(9), ParcheminRespecialisation(10),
		SphereReunion(11),  Cle1(13), Cle2(14),
		NbObjets(17);

		public final byte v;
		private ObjetsDonjon(int value) {
            v = (byte)value;
        }
	}
}
