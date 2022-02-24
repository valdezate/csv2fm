
package es.urjc.ccia.ruva;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;

public class csv2fm {
	public String source="";
	public String target="";
	public void convert() {
		boolean result=false;
		result=csv2fm(source,target);
	}
	public csv2fm() {
		source="";
		target="";
	}

	public boolean csv2fm(String source, String target) {
		// convert from csv to xml FeatureIde Format
		//1. declarations
		boolean resul=true;
		ArrayList<String> textCSV=new ArrayList<>();
		ArrayList<String> textXML=new ArrayList<>();
		ArrayList<String> textRules=new ArrayList<>();

		//2. read file
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(source));
			String line = reader.readLine();
			line=line.trim();
			while (line != null) {
				//System.out.println(line);
				if (line.length()>0) {
					textCSV.add(line);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			resul=false;
		}	
		
		String myField="";
		String myParent="";
		String myLogicalRelType="";
		String myRelationshipType="";
		String myRelConIn="";
		String myProperty="";
		String myMultiplicity="";
		String myValue="";
		String[] myRelConInChunks;	
		String rela="";
		String manda="";
		String frase="";
		Stack pilaRel=new Stack();		
		Stack pilaPadres=new Stack();
		
		String myFieldPrev="";
		String myParentPrev="";
		String myLogicalRelTypePrev="";
		String myRelationshipTypePrev="";
		String myRelConInPrev="";
		String myPropertyPrev="";
		String myMultiplicityPrev="";
		String myValuePrev="";
		String[] myRelConInChunksPrev;		
		int tabulation=0;
		String topePila="";
		String value="";
		
		pilaRel.push("-");
		pilaPadres.push("-");
		
		
		//3. converting
		textXML.add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		textXML.add("<featureModel>");
		textXML.add("	<properties>");
		textXML.add("		<graphics key=\"showhiddenfeatures\" value=\"true\"/>");
		textXML.add("		<graphics key=\"legendposition\" value=\"1217,115\"/>");
		textXML.add("		<graphics key=\"legendautolayout\" value=\"false\"/>");
		textXML.add("		<graphics key=\"showshortnames\" value=\"false\"/>");
		textXML.add("		<graphics key=\"layout\" value=\"horizontal\"/>");
		textXML.add("		<graphics key=\"showcollapsedconstraints\" value=\"true\"/>");
		textXML.add("		<graphics key=\"legendhidden\" value=\"false\"/>");
		textXML.add("		<graphics key=\"layoutalgorithm\" value=\"1\"/>");
		textXML.add("	</properties>");
		textXML.add("<struct>");	
		tabulation++;

		textRules.add("<constraints>");
		
		boolean isEnd=false;
		int lineCounter=0;
		String line="";
		int x=0;
		String repeatedLine="";
		boolean myf=false;
		boolean isOntology=false;
		
		while (!isEnd) {
			boolean isLine=false;
		
			while ((!isEnd) && (!isLine)) {
				line=textCSV.get(x).trim();
				lineCounter++;
				System.out.println("Linea "+lineCounter+":"+line);
	
				if ((line.length()>0) && (lineCounter>1)) {
					isLine=true;
				}
				x++;
				if (x==textCSV.size()) {
					isEnd=true;
				}
			}
			line=line+"EOF";
			if (isLine) {
				// descomponemos linea
				rela="";
				manda="";
				String[] lineChunks=line.split(";");
				if (lineChunks.length==12) {
					lineChunks[11]=lineChunks[11].substring(0,lineChunks[11].length()-3);
					myField=lineChunks[0].trim();
					myParent=lineChunks[1].trim();
					myLogicalRelType=lineChunks[3].trim().toLowerCase();
					myRelationshipType=lineChunks[2].trim().toLowerCase();
					myRelConIn=lineChunks[4].trim();
					myProperty=lineChunks[5].trim().toLowerCase();
					myMultiplicity=lineChunks[6].trim().toLowerCase();
					myValue=lineChunks[7].trim().toLowerCase();
					if (myRelConIn.length()>0) {
						myRelConInChunks=myRelConIn.split(",");
					}
					isOntology=false;
					if ((myRelationshipType.contains("consistsof")) || (myRelationshipType.contains("consitsof"))) {
						isOntology=true;
					}
					if ((myParent.length()==0) && (myRelationshipType.length()==0) && (myLogicalRelType.length()==0)) {
						isOntology=true;
					}
					
					if (!isOntology) {
						topePila=(String) pilaPadres.peek();
						while (!myParent.contentEquals(topePila) && pilaPadres.size()>1) {
							String relac="";
							pilaPadres.pop();
							relac=(String) pilaRel.pop();
							frase="</"+relac+">";
							tabulation--;
							repeatedLine=new String(new char[tabulation]).replace("\0", " ");						
							textXML.add(repeatedLine+frase);
							topePila=(String) pilaPadres.peek();
		 				
						}	
						if (myLogicalRelType.length()>0) {
							if (myLogicalRelType.contentEquals("and")) {
								rela="and";
							} else if (myLogicalRelType.contentEquals("or")) {
								rela="or";
							} else if (myLogicalRelType.contentEquals("xor")) {
								rela="alt";
							} else if (myRelationshipType.contentEquals("consistsof")) {
								rela="cons";
							} else {
								System.out.println("ERRORRR");
							}
							pilaPadres.add(myField);
							pilaRel.add(rela);
							
							if (myProperty.contentEquals("mandatory")) {
								manda=" mandatory=\"true\" ";
							} else if (myProperty.contentEquals("optional")) {
								manda="";
							}
							if (myValue.length()>0) {
								value=" value=\""+myValue+"\" ";
							} else  {
								value="";
							}
							frase="<"+rela+manda+" name=\""+myField+"\" "+value+">";
							repeatedLine=new String(new char[tabulation]).replace("\0", " ");						
							textXML.add(repeatedLine+frase);
							tabulation++;					
						}else {
							if (myProperty.contentEquals("mandatory")) {
								manda=" mandatory=\"true\" ";
							} else if (myProperty.contentEquals("optional")) {
								manda="";
							} else {
								manda=" mandatory=\"true\" ";
							}
							
							if (myValue.length()>0) {
								value=" value=\""+myValue+"\" ";
							} else  {
								value="";
							}
		
							frase="<feature "+manda+"name=\""+myField+"\""+value+"/>";
							repeatedLine=new String(new char[tabulation]).replace("\0", " ");						
							textXML.add(repeatedLine+frase);
						}

						if (myRelationshipType.length()>0) {
							if (myRelationshipType.contentEquals("consistsof")) {
								
							}else if (myRelationshipType.contentEquals("requires")) {
								textRules.add("<rule>");
								textRules.add("  <imp>");
								textRules.add("    <var>"+myField+"</var>");
								textRules.add("    <var>"+myRelConIn+"</var>");
								textRules.add("  </imp>");
								textRules.add("</rule>");						
							}else if (myRelationshipType.contentEquals("excludes")) {
								textRules.add("<rule>");
								textRules.add("  <not>");
								textRules.add("    <var>"+myField+"</var>");
								textRules.add("    <var>"+myRelConIn+"</var>");
								textRules.add("  </not>");
								textRules.add("</rule>");													
							} else { 
							
							}
						
						} 	
	 
						myFieldPrev=myField;
						myParentPrev=myParent;
						myRelationshipTypePrev=myRelationshipType;
						myLogicalRelTypePrev=myLogicalRelType;
						myRelConInPrev=myRelConIn;
						myPropertyPrev=myProperty;
						myMultiplicityPrev=myMultiplicity;
						myValuePrev=myValue;
					}
				}		
			}
		}
		while (pilaRel.size()>1) {
			String relac="";
			pilaPadres.pop();
			relac=(String) pilaRel.pop();
			frase="</"+relac+">";
			tabulation--;
			repeatedLine=new String(new char[tabulation]).replace("\0", " ");						
			textXML.add(repeatedLine+frase);			
		}
		
		textRules.add("</constraints>");
			
		textXML.add("</struct>");
		
		if (textRules.size()>2) {
			for (int i = 0; i < textRules.size(); i++){
				textXML.add(textRules.get(i));
			}
		}
		textXML.add("</featureModel>");			
 
		//4. writeing
		writeFile(target,textXML);
		return resul;		
	}
	private boolean writeFile(String thisPath, ArrayList<String> textBlock) {
		// mode 0 FAMA 1 mine
		boolean resul=true;
		boolean flagFound=false;

		FileWriter fichero = null;
		PrintWriter pw = null;
		try {
			fichero = new FileWriter(thisPath);
			pw = new PrintWriter(fichero);
			for (int i = 0; i < textBlock.size(); i++) {
				//System.out.println("Procesando línea "+i);
				String line;
				line=textBlock.get(i);
				pw.append(line+"\n");
				//pw.println(line);
				//System.out.println(fmFile.get(i));
			}

		} catch (Exception e) {
			resul=false;
			System.out.println("ERROR: Fichero no existe: "+thisPath);
			System.exit(19);
		} finally {
			try {
				// Ensure file is closed at the end 
				if (null != fichero)
					fichero.close();
			} catch (Exception e2) {
				e2.printStackTrace();
				resul=false;
			}
		}		
		return resul;
	}	
}
