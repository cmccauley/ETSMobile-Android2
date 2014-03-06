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

import ca.etsmtl.applets.etsmobile.http.soap.ExtendedSoapSerializationEnvelope;

public class listeDesActivitesEtProf extends DonneesRetournees implements
		KvmSerializable {

	public ArrayOfHoraireActivite listeActivites = new ArrayOfHoraireActivite();

	public ArrayOfEnseignant listeEnseignants = new ArrayOfEnseignant();

	public listeDesActivitesEtProf() {
	}

	public listeDesActivitesEtProf(AttributeContainer inObj,
			ExtendedSoapSerializationEnvelope envelope) {
		super(inObj, envelope);
		if (inObj == null)
			return;

		SoapObject soapObject = (SoapObject) inObj;

		if (soapObject.hasProperty("listeActivites")) {
			SoapObject j = (SoapObject) soapObject
					.getProperty("listeActivites");
			listeActivites = new ArrayOfHoraireActivite(j, envelope);
		}
		if (soapObject.hasProperty("listeEnseignants")) {
			SoapObject j = (SoapObject) soapObject
					.getProperty("listeEnseignants");
			listeEnseignants = new ArrayOfEnseignant(j, envelope);
		}

	}

	@Override
	public Object getProperty(int propertyIndex) {
		int count = super.getPropertyCount();
		if (propertyIndex == count + 0) {
			return listeActivites;
		}
		if (propertyIndex == count + 1) {
			return listeEnseignants;
		}
		return super.getProperty(propertyIndex);
	}

	@Override
	public int getPropertyCount() {
		return super.getPropertyCount() + 2;
	}

	@Override
	public void getPropertyInfo(int propertyIndex,
			@SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
		int count = super.getPropertyCount();
		if (propertyIndex == count + 0) {
			info.type = PropertyInfo.VECTOR_CLASS;
			info.name = "listeActivites";
			info.namespace = "http://etsmtl.ca/";
		}
		if (propertyIndex == count + 1) {
			info.type = PropertyInfo.VECTOR_CLASS;
			info.name = "listeEnseignants";
			info.namespace = "http://etsmtl.ca/";
		}
		super.getPropertyInfo(propertyIndex, arg1, info);
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
	}

}
