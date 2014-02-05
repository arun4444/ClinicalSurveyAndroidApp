package com.sippa.questionnaire;

/**
 * An object representing the EHR data that will be retrieved to the Sippa Server.
 * 
 * 
 * @author Touseef Hasan
 *
 */


public class EHRPostResponseData 
{
	public static abstract class Entry
	{
	}	

	
	/**
	 * Section: Procedures
	 */
	public static class ProceduresEntry extends Entry
	{
		@Override
		public String toString()
		{
			return "";
		}
		
		public String procedureMethod;
		public String time;
		public String assignedEntityState;
		public String assignedEntityCountry;
		public String assignedEntityZIP;
		public String organizationName;
		public String assignedEntityStreet;
		public String DoctorName;
		public String procedureName;
		public String assignedEntityCity;				
	}	
		
	
	/**
	 * Section: PersonalInformation
	 */
	public static class PersonalInformationEntry extends Entry
	{
		public static class ParticipantInformation	
		{			
			public static class ParticipantCodes
			{				
                String participantCodeSystemName;
                String participantCodeSystem;
                String participantDisplayName;
                String participantCode;				
			}
			
			public static class ParticipantAddress
			{
				String participantCountry;
				String participantCity;
				String participantState;
				String participantZIP;
				String participantStreet;				
			}			
			
			public static class ProviderInformation
			{
	            public static class ProviderOrganizationAddress
	            {
	            	String providerOrganizationState;
	            	String providerOrganizationCountry;
	            	String providerOrganizationZIP;
	            	String providerOrganizationCity;
	            	String providerOrganizationStreet;	            	
	            }
				
	            public static class CustodianOrganizationInformation
	            {
	            	public static class CustodianOrganizationAddress
	            	{
	                    String custodianOrganizationZIP;
	                    String custodianOrganizationCity;
	                    String custodianOrganizationStreet;
	                    String custodianOrganizationCountry;
	                    String custodianOrganizationState;
	            	}
	            	CustodianOrganizationAddress custodianOrganizationAddress;
	            	String custodianOrganizationName;
	            	String custodianOrganizationPhoneNumber;
	            	String custodianOrganizationPhoneType;
	            	
	            }
	            
	            String providerOrganizationName;
	            String providerOrganizationPhoneUse;
	            
	            ProviderOrganizationAddress providerOrganizationAddress;
	            String providerOrganizationPhoneNumber;
			}
			
			String birthday;
			ParticipantCodes Codes;
			ParticipantAddress Address;
			
			String participantPrefix;
			String participantType;
			String participantLastName;
			String participantphoneType;
			String participantClassCode;
			String participantphoneNumber;
		}		
		
		@Override
		public String toString()
		{
			return "";
		}
		
		ParticipantInformation[] participantInformation;
	}	
	
	
		
	/**
	 * Section: Immunizations
	 */
	public static class ImmunizationsEntry extends Entry
	{
		public static class Data
		{
            String reaction;
            String performerName;
            String drugManufacturerCountryAddress;
            String drugManufacturerZIPAddress;
            String refusalReason;
            String drugManufacturerStreetAddress;
			String[] performerNumber;

			String freeTextProductName;
			String administeredDate;
			String lotNumber;
			String medicationSeriesNumber;
			String performerStateAddress;
			String performerStreetAddress;
			String performerCityAddress;
			String performerZIPAddress;
			String drugManufacturerCityAddress;
			String performerCountryAddress;
			String drugManufacturerName;
			String drugManufacturerStateAddress;
			String refusal;
		}
		
		Data[] data;
	}	
	
	
	/**
	 * Section: Problems
	 */
	public static class ProblemsEntry extends Entry
	{
		public static class Data
		{
            String startDate;
            String Severity;
            String age;
            String endDate;
            String prescribedMedication;
            String problemName;			
		}
		
		Data[] data;
	}	
	
	/**
	 * Section: Medication
	 */
	public static class MedicationEntry extends Entry
	{
		
		public static class Data
		{
			
			@Override
			public String toString()
			{
				String str="";				
				str += "Name: "+brandName+"\n";
				str += "  from: "+startTime+"\n";
				str += "  to: "+endTime+"\n";
				str += "\n";
				return str;
			}
			
            String reaction;
            String instructions;
            String reason;
            String statusOfMedication;
            String deliveryMethod;
            String dispenseDate;
            String vehicle;
            String endTime;
            String doseIndicator;
            String brandName;
            String providerCityAddress;
            String startTime;
            String quantityOrderedValue;
            String presecriptionNumber;
            String expires;
            String providerStreetAddress;
            String fills;
            String providerCountryAddress;
            String quantityOrderedUnit;
            String fillStatus;
            String site;
            String providerStateAddress;
            String form;
            String typeOfMedication;
            String periodValue;
            String maxDoseQuantity;
            String fullfillmentInstructions;
            String doseQuantity;
            String doseUnit;
            String quantityUnit;
            String providerName;
            String route;
            String fillNumber;
            String orderedDay;
            String orderNumber;
            String orderingProviderFirstNames;
            String quantityValue;
            String orderingProviderLastNames;
            String providerZIPAddress;
            String productName;
            String periodUnit;
		}				
				
		@Override
		public String toString()
		{
			String str = "";
			for(int i=0; i<data.length; i++)
				str += data[i].toString();
			return str;
		}
		
		Data[] data;
	}
		

	/**
	 * Section: VitalSignsEntry
	 */
	public static class VitalSignsEntry extends Entry
	{
		public static class Data
		{
			public static class Components
			{
                String unit;
                String specificEntryDate;
                String value;
                String vitalName;				
			}						
			String creationDate;
			Components components;
		}		
		Data[] data;
	}
	
	
	
	/**
	 * Section: Results
	 */
	public static class ResultsEntry extends Entry
	{
		public static class Data
		{
			public static class Components
			{
                String resultStatus;
                String resultValue;
                String resultName;
                String resultAuthor;
                String resultUnit;				
			}			
			Components components;
		}
		Data[] data;
	}
	
	/**
	 * Section: Allergies
	 */
	public static class AllergiesEntry extends Entry
	{
		public static class Data 
		{
			@Override
			public String toString()
			{
				String str="Allergic to: "+allergicTo+"\n";
				str+="  Reaction: "+adverseEventReaction;
				str+="\n";
				return str;
			}			
			
            String adverseEventReactionCodeSystemName;
            String adverseEventProductCodeDisplayName;
            String adverseEventSeverityMagnitude;
            String adverseEventReaction;
            String adverseEventTypeCode;
            String adverseEventProductCodeSystemName;
            String adverseEventSeverityCode;
            String adverseEventTypeCodeSystem;
            String adverseEventProductCode;
            String adverseEventTypeCodeSystemName;
            String adverseEventReactionCodeDisplayName;
            String adverseEventSeverity;
            String adverseEventTypeCodeDisplayName;
            String allergicTo;
            String adverseEventSeverityCodeSystemName;
            String firstNoticed;
            String adverseEventReactionCodeSystem;
            String adverseEventSeverityCodeDisplayName;
            String adverseEventSeverityCodeSystem;
            String adverseEventReactionCode;
            String adverseEventProduct;
            String adverseEventProductCodeSystem;			
		}
		
		@Override
		public String toString()
		{
			String str="";
			for(int i=0; i<data.length; i++)
				str += data[i].toString();
			return str;
		}
		
		Data[] data;
	}
	
	/**
	 * Section: CareTeamEntry
	 */
	public static class CareTeamEntry extends Entry
	{
		public static class PatientInformation
		{
			public static class ParticipantInformation
			{
				public static class ParticipantCodes
				{
                    String participantCodeSystemName;
                    String participantCodeSystem;
                    String participantDisplayName;
                    String participantCode;
				}
				
				public static class ParticipantAddress
				{
                    String participantCountry;
                    String participantCity;
                    String participantState;
                    String participantZIP;
                    String participantStreet;					
				}
				
				ParticipantCodes Codes;
				String participantFirstName;
				ParticipantAddress Address;
				
                String participantPrefix;
                String participantType;
                String participantLastName;
                String participantphoneType;
                String participantClassCode;                
                String participantphoneNumber;				
			}
			
			public static class ProviderInformation
			{
				public static class ProviderOrganizationAddress
				{
	                  String providerOrganizationState;
	                  String providerOrganizationCountry;
	                  String providerOrganizationZIP;
	                  String providerOrganizationCity;
	                  String providerOrganizationStreet;
				}
	            String providerOrganizationName;
	            String providerOrganizationPhoneUse;
				ProviderOrganizationAddress providerOrganizationAddress;
				String providerOrganizationPhoneNumber;				
			}
			
			public static class CustodianOrganizationInformation
			{
				public static class CustodianOrganizationAddress
				{
	                  String custodianOrganizationZIP;
	                  String custodianOrganizationCity;
	                  String custodianOrganizationStreet;
	                  String custodianOrganizationCountry;
	                  String custodianOrganizationState;
				}
				
				CustodianOrganizationAddress custodianOrganizationAddress;
	            String custodianOrganizationName;
	            String custodianOrganizationPhoneNumber;
	            String custodianOrganizationPhoneType;				
			}
			
			static public class ComponentOfInformation
			{
				static public class EncounterParticipantInfo
				{
	                  String lastName;
	                  String prefix;
	                  String typeCode;
	                  String firstName;
	                  String idRoot;
	                  String healthCareFacilityID;					
				}
				
				static public class ComponentCode
				{
					String codeSystemName;
					String codeSystem;
					String code;
					String displayName;					
				}
				
				static public class EffectiveTimes
				{
	              String lowTime;
	              String highTime;
				}
				
				static public class ResponsibleParty
				{
					String lastName;
					String firstName;
					String idRoot;
					String idExtension;					
				}
				
				EncounterParticipantInfo encounterParticipantInfo;
				ComponentCode Codes;
				EffectiveTimes effectiveTimes;
				ResponsibleParty responsibleParty;				
	            String idRoot;
	            String idExtension;				
			}
			
			static public class InformantInformation
			{
				static public class InformantAddress
				{
                    String informantState;
                    String informantStreet;
                    String informantCountry;
                    String informantCity;
                    String informantZIP;
				}
				
				static public class InformantContactInfo
				{
					String informantPhoneType;
					String informantFirstName;
					String informantPhoneNumber;
					String informantLastName;
				}
				
				
				InformantAddress[] informantAddress;
				InformantContactInfo[] informantContactInfo;
			}
			
            String birthday;
            ParticipantInformation[] participantInformation;
            ProviderInformation providerInformation;
            CustodianOrganizationInformation custodianOrganizationInformation;
            ComponentOfInformation componentOfInformation;
            InformantInformation informantInformation;
		}
		
		PatientInformation patientInformation;
	}
		
	public MedicationEntry Medications;
	public AllergiesEntry Allergies;
	
}
