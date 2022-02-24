package es.urjc.ccia.ruva;

public class test {

	public test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean result;
		csv2fm conversion=new csv2fm();
		conversion.source="repo/meterological.csv";
		conversion.target="repo/meterological.xml";
		conversion.convert();
		

	}

}
