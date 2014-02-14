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

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class StudentProfile extends Vector<String> implements KvmSerializable{
	private static final long serialVersionUID = 3340376930647772523L;

	private String nom, prenom, codePerm, soldeTotal;

	private List<StudentPrograms> programms;

	public StudentProfile() {
	}

	public StudentProfile(final String nom, final String prenom,
			final String codePerm, final String solde) {
		this.nom = nom;
		this.prenom = prenom;
		this.codePerm = codePerm;
		soldeTotal = solde;
	}

	@Override
	public Object getProperty(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public String getCodePerm() {
		return codePerm != null ? codePerm.trim() : "";
	}

	public String getNom() {
		return nom != null ? nom.trim() : "";
	}

	public String getPrenom() {
		return prenom != null ? prenom.trim() : "";
	}

	public String getSolde() {
		return soldeTotal != null ? soldeTotal.trim() : "";
	}

	
	@Override
	public String toString() {
		return "" + nom + "" + prenom + "" + codePerm + "" + soldeTotal;
	}

	public void setStudentPrograms(List<StudentPrograms> programms2) {
		this.programms = programms2;
	}

	public List<StudentPrograms> getStudentPrograms() {
		return programms;
	}

	public StudentPrograms getActiveStudentProfile() {

		StudentPrograms activeProgram = null;

		for (StudentPrograms program : programms) {
			if (program.getStatut().equals("actif")) {
				activeProgram = program;
			}
		}

		return activeProgram;
	}
}
