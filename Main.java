import java.lang.System;
import java.io.PrintStream;
import java.io.IOException;
import java.time.YearMonth;
import java.time.Month;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class Main {

	private static Scanner sc = new Scanner(System.in);
	private static YearMonth ym;
	private Set<Integer > holidays;

	public int SIZE;
	private int year;
	private int month;
	private int totDays;
	private class Employee {
		private String name;
		private int leave;
		private int pref;
		public Employee (String name, int pref, int leave) {
			this.name = name;
			this.leave = leave;
			this.pref = pref;
		}
		public void setName (String name) { this.name = name;}
		public String getName () { return name;}
		public void setPref (int pref) { this.pref = pref;}
		public int getPref () { return pref;}
		public void setRest (int leave) { this.leave = leave;}
		public int getRest () { return leave;}
		public String toString () { return "Name: " + name;}
	}
	private ArrayList<Employee > employees;
	private class Row {
		private Set<Integer> shiftM, shiftD, shiftE, leave, ASHOLLEAVE;
		private boolean rest, isHol;
		public Row (Set<Integer > shiftM, Set<Integer > shiftD, Set<Integer > shiftE, Set<Integer > leave, Set<Integer > ASHOLLEAVE, boolean isHol) {
			this.shiftM = shiftM;
			this.shiftD = shiftD;
			this.shiftE = shiftE;
			this.leave  = leave;
			this.ASHOLLEAVE = ASHOLLEAVE;
			this.isHol  = isHol;
		}
		public Set<Integer > getM () { return shiftM;}
		public void setM (Set<Integer > M) { this.shiftM = shiftM;}
		public Set<Integer > getD () { return shiftD;}
		public void setD (Set<Integer > D) { this.shiftD = shiftD;}
		public Set<Integer > getE () { return shiftE;}
		public void setE (Set<Integer > E) { this.shiftE = shiftE;}
		public Set<Integer > getRest () { return leave;}
		public void setRest (Set<Integer > leave) { this.leave = leave;}
		public boolean getIsHol () { return isHol;}
		public void setIsHol (boolean isHol) { this.isHol = isHol;}
		public Set<Integer > getL () { return this.ASHOLLEAVE; }
		public void setL (Set<Integer > ASHOLLEAVE) { this.ASHOLLEAVE = ASHOLLEAVE; }
	}
	private Row[] table;
	private String prevDue;
	private int YEAR;
	private String MONTH;

	public Main (int year, int month) {
		YEAR = year;
		MONTH = Month.of(month).name();;
		this.year = year;
		this.month = month;
		ym = YearMonth.of(year, month);
		totDays = ym.lengthOfMonth();
	}

	public void getHolidays () {
		holidays = new HashSet<Integer >();
		System.out.print("Enter number of holidays: ");
		int numH = sc.nextInt();
		while (numH-- > 0) { System.out.print("Enter date: ");holidays.add(sc.nextInt());	}
		for (int i = 1;i <= totDays;i++)
			if (ym.atDay(i).getDayOfWeek().name().equals("SUNDAY"))
				holidays.add(i);
		int i = 8;
		for(;;i++) if (ym.atDay(i).getDayOfWeek().name().equals("SATURDAY")) { holidays.add(i);break;}
		holidays.add(i + 14);
	}

	public boolean isHol (int i) { return holidays.contains(i); }

	public void getEmployees () {
		employees = new ArrayList<> ();
		int tempId = 0;
		System.out.println("Enter names of employees in morning shift(0 - exit):");
		sc.nextLine();
		for (;;) {
			String tempName = sc.nextLine();
			if (tempName.equals("0"))	break;
			Employee tempEmp = new Employee(tempName, 0, 0);
			employees.add(tempEmp);
			SIZE++;
		}
		System.out.println("Enter names of employees in day shift(0 - exit):");
		for (;;) {
			String tempName = sc.nextLine();
			if (tempName.equals("0"))	break;
			Employee tempEmp = new Employee(tempName, 1, 0);
			employees.add(tempEmp);
			SIZE++;
		}
		System.out.println("Enter names of employees in evening shift(0 - exit):");
		for (;;) {
			String tempName = sc.nextLine();
			if (tempName.equals("0"))	break;
			Employee tempEmp = new Employee(tempName, 2, 0);
			employees.add(tempEmp);
			SIZE++;
		}

		for (tempId = 0;tempId < SIZE; tempId++)
			System.out.println("Id: " + tempId + "\t" + employees.get(tempId));

		System.out.print("Any employees with rest? (y / n): ");
		char ch = sc.next().charAt(0);
		if (ch == 'y' || ch == 'Y')
			for (;;) {
				System.out.print("Enter id: ");
				tempId = sc.nextInt();
				if (tempId < 0 || tempId >= SIZE)		break;	
				System.out.print("Enter rest due: ");
				int tempLeave = sc.nextInt();
				if (tempLeave <= 0 || tempLeave > 30)	break;
				employees.get(tempId).setRest(tempLeave);
				System.out.println("Do you want to continue?");
				char ch1 = sc.next().charAt(0);
				if (ch1 == 'n' || ch1 == 'N')	break;
			}
		initPrev();
	}

	public void makeTable () {
		Set<Integer > m = new HashSet<Integer >();
		Set<Integer > d = new HashSet<Integer >();
		Set<Integer > e = new HashSet<Integer >();
		for (int tempId = 0;tempId < SIZE;tempId++) {
			switch (employees.get(tempId).getPref()) {
				case 0:
					m.add(tempId);
					break;
				case 1:
					d.add(tempId);
					break;
				case 2:
					e.add(tempId);
					break;
				default:
					break;
			}
		}
		table = new Row[totDays + 1];
		boolean res;
		for (int i = 1;i <= totDays;i++) {
			table[i] = isHol(i)
			? new Row(
				new HashSet<Integer >(),
				new HashSet<Integer >(),
				new HashSet<Integer >(),
				new HashSet<Integer >(),
				new HashSet<Integer >(),
				isHol(i)
			)
			: new Row(
				new HashSet<Integer >(m),
				new HashSet<Integer >(d),
				new HashSet<Integer >(e),
				new HashSet<Integer >(),
				new HashSet<Integer >(),
				isHol(i)
			);
		}
	}

	public String setToS (Set<Integer > s) {
		Set<String > tempNames = new HashSet<String >();
		for (int element : s)
			tempNames.add(employees.get(element).getName());
		return String.join("/", tempNames);
	}

	public void del (int tempRow, int tempId) {
		int tempL = 0;
		Set<Integer > tempM = table[tempRow].getM();
		if (tempM.contains(tempId)) {
			tempM.remove(tempId);
			table[tempRow].setM(tempM);
			++tempL;
		}

		// Day
		Set<Integer > tempD = table[tempRow].getD();
		if (tempD.contains(tempId)) {
			tempD.remove(tempId);
			table[tempRow].setD(tempD);
			++tempL;
		}

		// Evening
		Set<Integer > tempE = table[tempRow].getE();
		if (tempE.contains(tempId)) {
			tempE.remove(tempId);
			table[tempRow].setE(tempE);
			++tempL;
		}
		if (tempL > 0) {
			employees.get(tempId).setRest(employees.get(tempId).getRest() - tempL);
			Set<Integer > tempR = table[tempRow].getRest();
			tempR.add(tempId);
			table[tempRow].setRest(tempR);
		}
	}

	public void lv (int tempRow, int tempId) {
		int tempL = 0;
		Set<Integer > tempM = table[tempRow].getM();
		if (tempM.contains(tempId)) {
			tempM.remove(tempId);
			table[tempRow].setM(tempM);
			++tempL;
		}

		// Day
		Set<Integer > tempD = table[tempRow].getD();
		if (tempD.contains(tempId)) {
			tempD.remove(tempId);
			table[tempRow].setD(tempD);
			++tempL;
		}

		// Evening
		Set<Integer > tempE = table[tempRow].getE();
		if (tempE.contains(tempId)) {
			tempE.remove(tempId);
			table[tempRow].setE(tempE);
			++tempL;
		}
		if (tempL > 0) {
			employees.get(tempId).setRest(employees.get(tempId).getRest() - tempL);
			
			Set<Integer > tempR = table[tempRow].getL();
			tempR.add(tempId);
			table[tempRow].setL(tempR);
		}
	}

	public void add (int tempRow, int tempId) {
		del(tempRow, tempId);
		lv(tempRow, tempId);

		System.out.println("0 - Morning shift\r\n1 - Day shift\r\n2 - Evening shift\r\n");
		System.out.print("Enter shift: ");
		int tempShift = sc.nextInt();
		if (tempShift > 2 || tempShift < 0) { System.out.println("Invalid entry."); return; }
		
		// Add
		switch (tempShift) {
			case 0:
				// Morning
				Set<Integer > tempM = table[tempRow].getM();
				tempM.add(tempId);
				table[tempRow].setM(tempM);
				break;
			case 1:
				// Day
				Set<Integer > tempD = table[tempRow].getD();
				tempD.add(tempId);
				table[tempRow].setD(tempD);
				break;
			case 2:
				// Evening
				Set<Integer > tempE = table[tempRow].getE();
				tempE.add(tempId);
				table[tempRow].setE(tempE);
				break;
			default:
				break;
		}

		employees.get(tempId).setRest(employees.get(tempId).getRest() + 1);
		
		Set<Integer > tempR = table[tempRow].getRest();
		tempR.remove(tempId);
		table[tempRow].setRest(tempR);

		tempR = table[tempRow].getL();
		tempR.remove(tempId);
		table[tempRow].setL(tempR);
	}

	public void update () {
		for (;;) {
			System.out.print("Enter date to update(0 - exit): ");
			int tempRow = sc.nextInt();
			if (tempRow <= 0 || tempRow > totDays)	break;
			char ch2 = 'Y';
			while(ch2 == 'Y' || ch2 == 'y') {
				System.out.print("Allot an employee to a shift - 0\r\nGive rest to an employee - 1\r\nGive leave to an employee - 2\r\nEnter choice: ");
				int choice = sc.nextInt();
				if (choice > 2 || choice < 0)	{ System.out.println("Invalid entry."); break; }
				for (int tempId = 0; tempId < SIZE; tempId++)
					System.out.println("Id: " + tempId + "\t" + employees.get(tempId));
				System.out.print("\r\nEnter id of employee: ");
				int tempId = sc.nextInt();
				if (tempId >= employees.size() || tempId < 0)	{ System.out.println("Invalid entry."); break; }

				if (choice == 1) 		del(tempRow, tempId);
				else if (choice == 0)	add(tempRow, tempId);
				else 					lv(tempRow, tempId);
				System.out.print("Do you want to continue? (y/n) ");
				ch2 = sc.next().charAt(0);
			}
			display();
		}
	}

	public String centerS (String s, int width) {
		int padSize = width - s.length();
		int padStart = s.length() + padSize / 2;
		s = String.format("%" + padStart + "s", s);
		s = String.format("%-" + width  + "s", s);
		return s;
	}

	public void display () {
		int datel = 5;
		int dayl  = 5;
		int msl   = 20;
		int dsl   = 20;
		int esl   = 20;
		int restl = 15;
		int leavel= 15;
		System.out.println("\r\n" + centerS("DUTY ROSTER OF JR.ENGINEERS(IT)(CONSOLE) FOR THE MONTH OF", 100));
		System.out.println("\r\n" + centerS(MONTH + "-" + YEAR, 100));
		System.out.print("\r\n" + centerS("Date", datel));
		System.out.print(centerS("Day", dayl));
		System.out.print(centerS("Morning shift", msl));
		System.out.print(centerS("Day shift", dsl));
		System.out.print(centerS("Evening shift", esl));
		System.out.print(centerS("Rest", restl));
		System.out.print(centerS("Leave", leavel));
		System.out.println("\r\n");
		for (int i = 1; i <= totDays;i++) {
			// Date
			System.out.print(centerS(Integer.toString(i), datel));
			// Day
			System.out.print(centerS(ym.atDay(i).getDayOfWeek().name().substring(0, 3), dayl));
			// Morning shift
			System.out.print(centerS(setToS(table[i].getM()), msl));
			// Day shift
			if (table[i].getD().isEmpty()) {
				if (table[i].getM().isEmpty() && table[i].getE().isEmpty())
					System.out.print(centerS("Rest to all", dsl));
				else
					System.out.print(centerS("", dsl));
			} else
				System.out.print(centerS(setToS(table[i].getD()), dsl));
			// Evening shift
			System.out.print(centerS(setToS(table[i].getE()), dsl));
			// Rest
			if (isHol(i) && !(table[i].getM().isEmpty() && table[i].getD().isEmpty() && table[i].getE().isEmpty()))
				System.out.print(centerS("Rest to others", restl));
			else
				System.out.print(centerS(setToS(table[i].getRest()), restl));
			// Leave
			System.out.print(centerS(setToS(table[i].getL()), leavel));
			System.out.println("\r\n");
		}
		analytics();
		System.out.println("\r\n\r\n" + centerS("", 65) + "SR.ENGINEER(IT)/IT CENTRE");
		System.out.println(centerS("", 70) + "(Console Operation)");
	}

	public void initPrev () {
		String T_buffer = "\r\nRest due for previous month : ";
		String buffer = "";
		for (Employee emp : employees) {
			int tempRest = emp.getRest();
			if (tempRest > 0)	buffer += "\r\n" + centerS(emp.getName(), 10) + "\t" + Integer.toString(tempRest) + " day" + (tempRest > 1 ? "s" : "") + " due";
		}
		if (buffer == "")
			buffer = T_buffer + "NIL";
		else
			buffer = T_buffer + buffer;
		prevDue = buffer;
	}

	public void analytics () {
		System.out.print("\r\n" + prevDue + "\r\n\r\nRest due for current month : ");
		String buffer = "";
		for (Employee emp : employees) {
			int tempRest = emp.getRest();
			if (tempRest > 0)	buffer += "\r\n" + centerS(emp.getName(), 10) + "\t" + Integer.toString(Math.max(0, tempRest)) + " day" + (Math.max(0, tempRest) > 1 ? "s" : "") + " due";
		}
		if (buffer == "")
			buffer += "NIL";
		System.out.println(buffer);
	}

	public static void main (String[] args) throws IOException, InterruptedException {
		System.out.print("Enter year: ");
		int year = sc.nextInt();
		
		System.out.print("Enter month (1 - 12) : ");
		int month = sc.nextInt();

		Main obj = new Main(year, month);
		obj.getHolidays();
		obj.getEmployees();
		obj.makeTable();
		obj.display();
		obj.update();
		System.out.print("Do you want to print? (y / n)");
		char ch = sc.next().charAt(0);
		System.setOut(new PrintStream(".\\roster.txt"));
		obj.display();
		if (ch == 'y' || ch == 'Y')
			Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"print roster.txt\"");
	}
}