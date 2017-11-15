package hw3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Homework {

	String inputFilePath = "E://Assignments//Sem 1//AI//input.txt";
	String outputFilePath = "E://Assignments//Sem 1//AI//output.txt";
	int noOfQuery = 0;
	int noOfKB = 0;
	ArrayList<Literal> queries = new ArrayList<>();
	ArrayList<Clause> knowledge = new ArrayList<>();
	boolean[] ans;
	long startTime = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Homework hw = new Homework();
		hw.startTime = System.currentTimeMillis();
		hw.readInput();
		hw.runTheoremProver();
		hw.writeOutput();
	}

	public void writeOutput() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, false));
			//write to output file
			int i=1;
			for(boolean b:ans) {
				if(b) {
					writer.write("TRUE");
					System.out.println(i+". TRUE");
				}
				else {
					writer.write("FALSE");
					System.out.println(i+". FALSE");
				}
				i++;
				writer.newLine();
			}
			writer.close();
			long totalTime = System.currentTimeMillis()-startTime;
			System.out.println("Running Time : "+totalTime+" ms");
			System.out.println("Running Time : "+(totalTime/1000)+" s");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	void readInput() {
		//read input file
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
			try {
				String line = br.readLine();

				//get number of queries
				noOfQuery = Integer.parseInt(line.trim());

				//get queries
				for(int i=0;i<noOfQuery;i++) {
					line = br.readLine();

					String[] str = line.split("\\(");
					String name = str[0];
					str = str[1].split(",");
					String args[] = new String[str.length];
					int j=0;
					for(String s:str) {
						args[j] = s;
						j++;
					}
					//remove last bracket
					String last = args[args.length-1];
					args[args.length-1] = last.substring(0, last.length()-1);
					boolean isNot = false;
					if(name.startsWith("~")) {
						name = name.substring(1, name.length());
						isNot = true;
					}else if(name.startsWith("NOT")) {
						name = name.split("\\s+")[0];
						isNot = true;
					}

					Predicate predicate = new Predicate(name,args);
					Literal literal = new Literal(predicate,isNot);
					queries.add(literal);

				}

				//System.out.println(queries);

				//get no of KB sentences
				line = br.readLine();
				noOfKB = Integer.parseInt(line.trim());

				//get sentences
				for(int i=0;i<noOfKB;i++) {
					line = br.readLine();  
					convertToClause(line);
				}

				//System.out.println(knowledge);

			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				br.close();
			}
		}catch(FileNotFoundException fe) {
			System.out.println("File Not Found");
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

	void convertToClause(String line) {
		//get knowledge base sentences	
		String separator = "OR";
		if(line.contains("|")) {
			separator = "\\|";
		}
		ArrayList<Literal> literals = new ArrayList<>();
		String[] clauses = line.split(separator);
		for(String clause:clauses) {
			String[] str = clause.split("\\(");
			String name = str[0];
			str = str[1].split(",");
			String args[] = new String[str.length];
			int j=0;
			for(String s:str) {
				args[j] = s;
				j++;
			}
			//remove last bracket
			String last = args[args.length-1];
			args[args.length-1] = last.substring(0, last.length()-1);
			boolean isNot = false;
			if(name.startsWith("~")) {
				name = name.substring(1, name.length());
				isNot = true;
			}else if(name.startsWith("NOT")) {
				name = name.split("\\s+")[0];
				isNot = true;
			}

			Predicate predicate = new Predicate(name,args);
			Literal literal = new Literal(predicate,isNot);
			literals.add(literal);
		}
		knowledge.add(new Clause(literals));
	}

	void runTheoremProver() {

		ans = new boolean[noOfQuery];

		for(int i=0;i<noOfQuery;i++) {
			//solve one by one queries
			ArrayList<Clause> KB = new ArrayList<>(knowledge);
			Literal query = queries.get(i);
			query.negate();
			ArrayList<Literal> q = new ArrayList<>();
			q.add(query);
			ans[i] = check(new Clause(q), KB);
		}

	}

	boolean check(Clause c,ArrayList<Clause> kb) {
		boolean result = false;
		for(Clause ci:kb) {
			//System.out.println(kb);
			Clause resolvent = resolve(c, ci);
			//System.out.println(c);
			//System.out.println(ci);
			if(c.getClause().size()==0) {
				result = true;
				break;
			}
			boolean f = true;
			if(resolvent!=null) {
				//System.out.println(resolvent);
				if(resolvent.getClause().size()==0) {
					result = true;
					break;
				}
				ArrayList<Clause> temp = new ArrayList<>(kb);
				temp.remove(ci);
				//System.out.println(temp);
				f = check(new Clause(resolvent), temp);
				if(f==true) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	Clause resolve(Clause c1,Clause c2) {
		Clause ct1 = new Clause(c1);
		Clause ct2 = new Clause(c2);
		//ArrayList<Literal> li1 = new ArrayList<>(c1.getClause());
		//ArrayList<Literal> li2 = new ArrayList<>(c2.getClause());
		HashMap<String,String> substituitionSet = new HashMap<>();
		for(Literal l1:c1.getClause()) {
			for(Literal l2:c2.getClause()) {
				//System.out.println(l1);
				//System.out.println(l2);
				if(l1.equals(l2) && l1.isNot != l2.isNot) {
					//unification
					HashMap<String,String> temp = unify(l1, l2);
					if(temp!=null) {
						//can be simplified
						ct1.getClause().remove(l1);
						ct2.getClause().remove(l2);
						substituitionSet.putAll(temp);
					}

				}
			}
		}
		if(substituitionSet.size()>0) {
			for(String var:substituitionSet.keySet()) {
				String constant = substituitionSet.get(var);
				ArrayList<Literal> l1 = ct1.getClause();
				ArrayList<Literal> l2 = ct2.getClause();
				//set clause ct1
				for(int j=0;j<l1.size();j++) {
					Literal l = l1.get(j);
					String args[] = l.getPredicate().getArgs();
					for(int i=0;i<args.length;i++) {
						if(args[i].equals(var)) {
							args[i] = constant;
						}
					}
					l.getPredicate().setArgs(args);
					l1.set(j, l);
				}
				ct1.setClause(l1);

				//set clause 2
				for(int j=0;j<l2.size();j++) {
					Literal l = l2.get(j);
					String args[] = l.getPredicate().getArgs();
					for(int i=0;i<args.length;i++) {
						if(args[i].equals(var)) {
							args[i] = constant;
						}
					}
					l.getPredicate().setArgs(args);
					l2.set(j, l);
				}
				ct2.setClause(l2);
			}
			return mergeClauses(ct1, ct2);
		}else if(substituitionSet.size()==0) {
			if(ct1.getClause().size()==0 && ct2.getClause().size()==0)
				return new Clause(new ArrayList<Literal>());
			if(ct1.getClause().size()==0)
				return ct2;
			if(ct2.getClause().size()==0)
				return ct1;
		}
		return null;
	}

	Clause mergeClauses(Clause c1,Clause c2) {
		ArrayList<Literal> litersals = new ArrayList<>(c1.getClause());
		litersals.addAll(c2.getClause());
		return new Clause(litersals);
	}

	HashMap<String,String> unify(Literal l1,Literal l2) {
		HashMap<String, String> substituitionSet = new HashMap<>();
		String[] args1 = l1.getPredicate().getArgs();
		String[] args2 = l2.getPredicate().getArgs();
		for(int i=0;i<args1.length;i++) {
			String x = args1[i];
			String y = args2[i];

			if(!x.equals(y)) {
				if(Character.isUpperCase(x.charAt(0)) && Character.isUpperCase(y.charAt(0))) {
					//if both are constant then we can not unify literals
					if(!x.equals(y))
						return null;
				}else if(Character.isUpperCase(x.charAt(0))) {
					//x = constant and y= variable
					substituitionSet.put(y, x);
				}else if(Character.isUpperCase(y.charAt(0))) {
					//x = variable and y = Constant
					substituitionSet.put(x, y);
				}else {
					//both are variables
					substituitionSet.put(x, y);
				}
			}
		}
		return substituitionSet;
	}

	class Predicate{
		String name;
		String[] args;

		Predicate() {
		}
		Predicate(String name, String[] args) {
			this.name = name;
			this.args = args;
		}
		Predicate(Predicate predicate){
			this.name = predicate.getName();
			String[] oldArgs = predicate.getArgs();
			String[] newArgs = new String[oldArgs.length];
			for(int i=0;i<oldArgs.length;i++) {
				newArgs[i] = oldArgs[i];
			}
			this.args = newArgs;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String[] getArgs() {
			return args;
		}
		public void setArgs(String[] args) {
			this.args = args;
		}

		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			if(this.getName().equals(((Predicate)obj).getName()))
				return true;
			return false;
		}
	}

	class Literal{

		Predicate predicate;
		boolean isNot;

		Literal(){}

		Literal(Predicate predicate,boolean isNot){
			this.predicate = predicate;
			this.isNot = isNot;
		}

		Literal(Literal literal){
			this.isNot = literal.isNot;
			this.predicate = new Predicate(literal.getPredicate());
		}

		public boolean isNot() {
			return isNot;
		}

		public void setNot(boolean isNot) {
			this.isNot = isNot;
		}

		public Predicate getPredicate() {
			return predicate;
		}

		public void setPredicate(Predicate predicate) {
			this.predicate = predicate;
		}

		public void negate() {
			boolean negation = isNot?false:true;
			this.setNot(negation);
		}

		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			if(this.getPredicate().equals(((Literal) obj).getPredicate()))
				return true;
			return false;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			if(isNot)
				sb.append("~");
			sb.append(predicate.getName()+"(");
			String args[] = predicate.getArgs();
			for(String s:args) {
				sb.append(s+",");
			}
			sb = new StringBuilder(sb.substring(0, sb.length()-1));
			sb.append(")");
			return sb.toString();
		}

	}

	class Clause{

		ArrayList<Literal> clause;

		public Clause() {
		}

		public Clause(ArrayList<Literal> clause) {
			this.clause = clause;
		}

		public Clause(Clause clause) {
			ArrayList<Literal> newList = new ArrayList<>();
			for(Literal l:clause.getClause()) {
				Literal newL = new Literal(l);
				newList.add(newL);
			}
			this.clause = newList;
		}

		public ArrayList<Literal> getClause() {
			return clause;
		}

		public void setClause(ArrayList<Literal> clause) {
			this.clause = clause;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			for(Literal literal:clause) {
				sb.append(literal);
				sb.append(" | ");
			}
			if(sb.length()>3)
				sb = new StringBuilder(sb.substring(0, sb.length()-3));
			return sb.toString();
		}

	}

}
