package es.urjc.ccia.ruva;

public class test {

	public test() {
	}

	public static void main(String[] args) {
		boolean result;
		csv2fm conversion=new csv2fm();
		conversion.source="meterological.csv";
		conversion.target="meterological.xml";
		conversion.convert();
		

	}

}
