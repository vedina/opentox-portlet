package net.ideaconsult.opentox.compound;

public enum _titles { //from OpenTox.owl
	Compound,
	CASRN {
		@Override
		public String getTitle() {
			return "CASRN";
		}
	},
	EINECS,
	IUPACName {
		@Override
		public String getTitle() {
			return "IUPAC name";
		}
	},
	ChemicalName {
		@Override
		public String getTitle() {
			return "Chemical Name";
		}
	},
	SMILES,
	InChI_std {
		@Override
		public String getTitle() {
			return "Standard InChI";
		}
	},
	InChIKey_std {
		@Override
		public String getTitle() {
			return "Standard InChI key";
		}
	},
	REACHRegistrationDate {
		@Override
		public String getTitle() {
			return "REACH registration date";
		}
	};
	public String getTitle() {
		return name();
	}
}