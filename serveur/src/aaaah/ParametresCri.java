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

package aaaah;

public class ParametresCri {
	private final int TEMPS_CRI_NORMAL = 10000;
	private final int PUISSANCE_CRI_NORMAL = 4;
	
	private final int TEMPS_CRI_NO_CONTA = 15000;
	private final int PUISSANCE_CRI_NO_CONTA = 6;
	
	private int TempsEntreCri = TEMPS_CRI_NORMAL;
	private int PuissanceCriX = PUISSANCE_CRI_NORMAL;
	
	private final int TempsCriOriginel;
	private final int PuissanceCriOriginel;
	
	private boolean ParamOntEteModif = false;
	
	public ParametresCri(boolean SansConta, boolean MS) {
		if (SansConta) {
			TempsCriOriginel = (MS ? 10000 : TEMPS_CRI_NO_CONTA);
			PuissanceCriOriginel = PUISSANCE_CRI_NO_CONTA;
		} else {
			TempsCriOriginel = (MS ? 10000 : TEMPS_CRI_NORMAL);
			PuissanceCriOriginel = PUISSANCE_CRI_NORMAL;
		}
		
		TempsEntreCri = TempsCriOriginel;
		PuissanceCriX = PuissanceCriOriginel;
		ResetValidite();
	}
	
	private boolean SontValeursOriginelles() {
		return TempsEntreCri == TempsCriOriginel && PuissanceCriX == PuissanceCriOriginel;
	}
	
	public boolean NextCartesSerontValides() {
		return SontValeursOriginelles();
	}
	
	public void ResetValidite() {
		ParamOntEteModif = !NextCartesSerontValides(); // Reste à true si les cartes ne seront pas valides dès le début
	}
	
	/**
	 * La carte est valide si les paramètres du cri n'ont jamais été changés
	 * @return
	 */
	public boolean CarteValide() {
		return !ParamOntEteModif && SontValeursOriginelles();
	}
	
	public String MessageDescriCommandeCri() {
		return "/cri Puissance {Durée} : modifier la puissance des cris (0 à 10) et la durée de récupération en millisecondes (100 à 50000) de ceux-ci (Durée étant facultatif)."
				+ "\n - Vous pouvez remplacer Puissance ou Durée par * pour conserver les paramètres actuels."
				+ "\n - Pour mettre les cris de base utilisez \"/cri normal\", pour ceux en ng utilisez \"/cri noconta\" et \"/cri off\" pour remettre à la normale."
				+ "\nImportant : cette commande retire la possibilité de faire des records.";
	}
	
	/**
	 * @param Puissance : puissance des cris ou base/ng/off/*
	 * @param DureeRechargeMS : durée de recharge ou * ou vide (pas de modif)
	 * 
	 * Retourne le texte à renvoyer à l'auteur de la commande.
	 */
	public String SetCris(String Puissance, String DureeRechargeMS) {
		if (DureeRechargeMS.equals("")) {
			if (Puissance.equalsIgnoreCase("normal")) {
				PuissanceCriX = PUISSANCE_CRI_NORMAL;
				TempsEntreCri = TEMPS_CRI_NORMAL;
			} else if (Puissance.equalsIgnoreCase("noconta")) {
				PuissanceCriX = PUISSANCE_CRI_NO_CONTA;
				TempsEntreCri = TEMPS_CRI_NO_CONTA;
			} else if (Puissance.equalsIgnoreCase("off")) {
				PuissanceCriX = PuissanceCriOriginel;
				TempsEntreCri = TempsCriOriginel;
				
				// (ParamOntEteModif = ParamOntEteModif)
				
				return "La puissance et la durée entre deux cris ont été remis à leur valeur par défaut. Les records seront réactivés à la prochaine carte.";
			}
			
			else if (Puissance.equals("*")) {
				// Ne rien faire
			} else {
				try {
					PuissanceCriX = Math.max(0, Math.min(10, Integer.parseInt(Puissance)));
				} catch (Exception e) {
					// (ParamOntEteModif = ParamOntEteModif)
					return MessageDescriCommandeCri();
				}
			}
			
			ParamOntEteModif = (ParamOntEteModif || !SontValeursOriginelles());
			
			return "Puissance des cris : " + PuissanceCriX + ".\nDurée entre deux cris : " + TempsEntreCri + "ms.";
		} else {
			try {
				if (!Puissance.equals("*")) {
					PuissanceCriX = Math.max(0, Math.min(10, Integer.parseInt(Puissance)));
				}
				
				if (!DureeRechargeMS.equals("*")) {
					TempsEntreCri = Math.max(100, Math.min(50000, Integer.parseInt(DureeRechargeMS)));
				}
				
			} catch (Exception e) {
				ParamOntEteModif = (ParamOntEteModif || !SontValeursOriginelles());
				return MessageDescriCommandeCri();
			}
			
			ParamOntEteModif = (ParamOntEteModif || !SontValeursOriginelles());
			
			return "Puissance des cris : " + PuissanceCriX + ".\nDurée entre deux cris : " + TempsEntreCri + "ms.";
		}
	}
	
	public String toString() {
		return PuissanceCriX + ";" + TempsEntreCri;
	}

	public int DureeSafeEntreCris() {
		// On laisse un peu de marge
		return Math.min(8000, TempsEntreCri);
	}
	
}
