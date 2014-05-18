package ca.etsmtl.applets.etsmobile.model;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 2.0.0.4
//
// Created by Quasar Development at 15-01-2014
//
//---------------------------------------------------

import java.util.Hashtable;

import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import ca.etsmtl.applets.etsmobile.http.soap.ExtendedSoapSerializationEnvelope;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "trimestre")
public class Trimestre extends AttributeContainer implements KvmSerializable {
	@DatabaseField
	public String abrege;
	@DatabaseField
	public String auLong;
	@DatabaseField
	public String dateDebut;
	@DatabaseField
	public String dateFin;
	@DatabaseField
	public String dateFinCours;
	@DatabaseField
	public String dateDebutChemiNot;
	@DatabaseField
	public String dateFinChemiNot;
	@DatabaseField
	public String dateDebutAnnulationAvecRemboursement;
	@DatabaseField
	public String dateFinAnnulationAvecRemboursement;
	@DatabaseField
	public String dateFinAnnulationAvecRemboursementNouveauxEtudiants;
	@DatabaseField
	public String dateDebutAnnulationSansRemboursementNouveauxEtudiants;
	@DatabaseField
	public String dateFinAnnulationSansRemboursementNouveauxEtudiants;
	@DatabaseField
	public String dateLimitePourAnnulerASEQ;
	@DatabaseField(id = true)
	public int id;

	public Trimestre() {
	}

	public Trimestre(AttributeContainer inObj, ExtendedSoapSerializationEnvelope envelope) {

		if (inObj == null)
			return;

		SoapObject soapObject = (SoapObject) inObj;

		if (soapObject.hasProperty("abrege")) {
			Object obj = soapObject.getProperty("abrege");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					abrege = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				abrege = (String) obj;
			}
		}
		if (soapObject.hasProperty("auLong")) {
			Object obj = soapObject.getProperty("auLong");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					auLong = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				auLong = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateDebut")) {
			Object obj = soapObject.getProperty("dateDebut");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateDebut = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateDebut = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateFin")) {
			Object obj = soapObject.getProperty("dateFin");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateFin = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateFin = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateFinCours")) {
			Object obj = soapObject.getProperty("dateFinCours");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateFinCours = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateFinCours = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateDebutChemiNot")) {
			Object obj = soapObject.getProperty("dateDebutChemiNot");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateDebutChemiNot = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateDebutChemiNot = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateFinChemiNot")) {
			Object obj = soapObject.getProperty("dateFinChemiNot");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateFinChemiNot = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateFinChemiNot = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateDebutAnnulationAvecRemboursement")) {
			Object obj = soapObject.getProperty("dateDebutAnnulationAvecRemboursement");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateDebutAnnulationAvecRemboursement = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateDebutAnnulationAvecRemboursement = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateFinAnnulationAvecRemboursement")) {
			Object obj = soapObject.getProperty("dateFinAnnulationAvecRemboursement");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateFinAnnulationAvecRemboursement = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateFinAnnulationAvecRemboursement = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateFinAnnulationAvecRemboursementNouveauxEtudiants")) {
			Object obj = soapObject.getProperty("dateFinAnnulationAvecRemboursementNouveauxEtudiants");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateFinAnnulationAvecRemboursementNouveauxEtudiants = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateFinAnnulationAvecRemboursementNouveauxEtudiants = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateDebutAnnulationSansRemboursementNouveauxEtudiants")) {
			Object obj = soapObject.getProperty("dateDebutAnnulationSansRemboursementNouveauxEtudiants");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateDebutAnnulationSansRemboursementNouveauxEtudiants = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateDebutAnnulationSansRemboursementNouveauxEtudiants = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateFinAnnulationSansRemboursementNouveauxEtudiants")) {
			Object obj = soapObject.getProperty("dateFinAnnulationSansRemboursementNouveauxEtudiants");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateFinAnnulationSansRemboursementNouveauxEtudiants = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateFinAnnulationSansRemboursementNouveauxEtudiants = (String) obj;
			}
		}
		if (soapObject.hasProperty("dateLimitePourAnnulerASEQ")) {
			Object obj = soapObject.getProperty("dateLimitePourAnnulerASEQ");
			if (obj != null && obj.getClass().equals(SoapPrimitive.class)) {
				SoapPrimitive j = (SoapPrimitive) obj;
				if (j.toString() != null) {
					dateLimitePourAnnulerASEQ = j.toString();
				}
			} else if (obj != null && obj instanceof String) {
				dateLimitePourAnnulerASEQ = (String) obj;
			}
		}

	}

	@Override
	public Object getProperty(int propertyIndex) {
		if (propertyIndex == 0) {
			return abrege;
		}
		if (propertyIndex == 1) {
			return auLong;
		}
		if (propertyIndex == 2) {
			return dateDebut;
		}
		if (propertyIndex == 3) {
			return dateFin;
		}
		if (propertyIndex == 4) {
			return dateFinCours;
		}
		if (propertyIndex == 5) {
			return dateDebutChemiNot;
		}
		if (propertyIndex == 6) {
			return dateFinChemiNot;
		}
		if (propertyIndex == 7) {
			return dateDebutAnnulationAvecRemboursement;
		}
		if (propertyIndex == 8) {
			return dateFinAnnulationAvecRemboursement;
		}
		if (propertyIndex == 9) {
			return dateFinAnnulationAvecRemboursementNouveauxEtudiants;
		}
		if (propertyIndex == 10) {
			return dateDebutAnnulationSansRemboursementNouveauxEtudiants;
		}
		if (propertyIndex == 11) {
			return dateFinAnnulationSansRemboursementNouveauxEtudiants;
		}
		if (propertyIndex == 12) {
			return dateLimitePourAnnulerASEQ;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		return 13;
	}

	@Override
	public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		if (propertyIndex == +0) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "abrege";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +1) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "auLong";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +2) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateDebut";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +3) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateFin";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +4) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateFinCours";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +5) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateDebutChemiNot";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +6) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateFinChemiNot";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +7) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateDebutAnnulationAvecRemboursement";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +8) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateFinAnnulationAvecRemboursement";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +9) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateFinAnnulationAvecRemboursementNouveauxEtudiants";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +10) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateDebutAnnulationSansRemboursementNouveauxEtudiants";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +11) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateFinAnnulationSansRemboursementNouveauxEtudiants";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == +12) {
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "dateLimitePourAnnulerASEQ";
			info.namespace = "http://etsmtl.ca/";
		}
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
	}

}
