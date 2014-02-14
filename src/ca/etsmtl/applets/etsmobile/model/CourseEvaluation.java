/*******************************************************************************
 * Copyright 2013 Club ApplETS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ca.etsmtl.applets.etsmobile.model;

import java.util.ArrayList;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;


public class CourseEvaluation implements KvmSerializable {

	private static final long serialVersionUID = 7314596338980655733L;

//	@SerializedName("noteACeJour")
	private String noteACeJour;

//	@SerializedName("scoreFinalSur100")
	private String scoreFinalSur100;

//	@SerializedName("moyenneClasse")
	private String moyenneClasse;

//	@SerializedName("ecartTypeClasse")
	private String ecartTypeClasse;

//	@SerializedName("medianeClasse")
	private String medianeClasse;

//	@SerializedName("rangCentileClasse")
	private String rangCentileClasse;

//	@SerializedName("noteACeJourElementsIndividuels")
	private String noteACeJourElementsIndividuels;

//	@SerializedName("noteSur100PourElementsIndividuels")
	private String noteSur100PourElementsIndividuels;

	private String cote;

//	@SerializedName("liste")
	private ArrayList<EvaluationElement> evaluationElements;

	@Override
	public Object getProperty(int index) {
		switch (index) {
		case 0:
			
			
			break;

		default:
			break;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 9;
	}

	@Override
	public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo arg2) {
		switch (index) {
		case 0:
			
			
			break;

		default:
			break;
		}		
	}

	@Override
	public void setProperty(int index, Object obj) {
		switch (index) {
		case 0:
			setNoteACeJour(obj.toString());
			break;
		case 1:
			setNoteACeJour(obj.toString());
			break;
		case 2:
			setNoteACeJour(obj.toString());
			break;
		case 3:
			setNoteACeJour(obj.toString());
			break;
		case 4:
			setNoteACeJour(obj.toString());
			break;
		case 5:
			setNoteACeJour(obj.toString());
			break;
		case 6:
			setNoteACeJour(obj.toString());
			break;
		case 7:
			setNoteACeJour(obj.toString());
			break;
		case 8:
			setNoteACeJour(obj.toString());
			break;

		default:
			break;
		}
	}
	
	public String getCote() {
		return cote;
	}

	public String getEcartTypeClasse() {
		return ecartTypeClasse;
	}

	public ArrayList<EvaluationElement> getEvaluationElements() {
		return evaluationElements;
	}

	public String getMedianeClasse() {
		return medianeClasse;
	}

	public String getMoyenneClasse() {
		return moyenneClasse;
	}

	public String getNoteACeJour() {
		return noteACeJour;
	}

	public String getNoteACeJourElementsIndividuels() {
		return noteACeJourElementsIndividuels;
	}

	public String getNoteSur100PourElementsIndividuels() {
		return noteSur100PourElementsIndividuels;
	}

	public String getRangCentileClasse() {
		return rangCentileClasse;
	}

	public String getScoreFinalSur100() {
		return scoreFinalSur100;
	}

	public void setCote(final String cote) {
		this.cote = cote;
	}

	public void setEcartTypeClasse(final String ecartTypeClasse) {
		this.ecartTypeClasse = ecartTypeClasse;
	}

	public void setEvaluationElements(final ArrayList<EvaluationElement> evaluationElements) {
		this.evaluationElements = evaluationElements;
	}

	public void setMedianeClasse(final String medianeClasse) {
		this.medianeClasse = medianeClasse;
	}

	public void setMoyenneClasse(final String moyenneClasse) {
		this.moyenneClasse = moyenneClasse;
	}

	public void setNoteACeJour(final String noteACeJour) {
		this.noteACeJour = noteACeJour;
	}

	public void setNoteACeJourElementsIndividuels(final String noteACeJourElementsIndividuels) {
		this.noteACeJourElementsIndividuels = noteACeJourElementsIndividuels;
	}

	public void setNoteSur100PourElementsIndividuels(final String noteSur100PourElementsIndividuels) {
		this.noteSur100PourElementsIndividuels = noteSur100PourElementsIndividuels;
	}

	public void setRangCentileClasse(final String rangCentileClasse) {
		this.rangCentileClasse = rangCentileClasse;
	}

	public void setScoreFinalSur100(final String scoreFinalSur100) {
		this.scoreFinalSur100 = scoreFinalSur100;
	}
}
