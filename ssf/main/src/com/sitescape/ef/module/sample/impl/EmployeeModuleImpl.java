package com.sitescape.ef.module.sample.impl;

import java.util.Collections;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.impl.AbstractModuleImpl;
import com.sitescape.ef.module.sample.Employee;
import com.sitescape.ef.module.sample.EmployeeModule;
import com.sitescape.ef.util.NLT;

public class EmployeeModuleImpl extends AbstractModuleImpl implements EmployeeModule {
	
	private SortedMap employees = Collections.synchronizedSortedMap(new TreeMap());

	public EmployeeModuleImpl() {
		addEmployee("Dave", "Griffin", new Integer(10000));
		addEmployee("Peter", "Hurley", new Integer(10000));
		addEmployee("Roy", "Klein", new Integer(10000));
		addEmployee("Janet", "McCann", new Integer(10000));
		addEmployee("Meyer", "Billmers", new Integer(10000));
		addEmployee("Mary", "Utt", new Integer(10000));
		addEmployee("Jong", "Kim", new Integer(10000));
	}
	
    public Employee getEmployee (Integer key) {
    	return (Employee)this.employees.get(key);
    }
    
    public SortedSet getAllEmployees () {
		// Just to see whether RequestContext works or not. 
		User user = RequestContextHolder.getRequestContext().getUser();

    	return (SortedSet) new TreeSet(this.employees.values());
    }
    
    public int addEmployee (Employee employee) {
        int key;
        synchronized (employees) {
            if (employees.isEmpty()) key = 1;
            else key = ((Integer)employees.lastKey()).intValue() + 1;
            Integer keyObj = new Integer(key);
            employee.setKey(keyObj);
            this.employees.put(keyObj, employee);
        }
        return key;
    }

    public int addEmployee (String author, String title, Integer count) {
        Employee employee = new Employee(author, title, count);
        return addEmployee(employee);
    }

    public void incrementSalary(Integer key, Integer increment) {
    	Employee employee = getEmployee(key);
    	employee.incrementSalary(increment);
    	
    	
		// Just to see whether RequestContext works or not. 
		User user = RequestContextHolder.getRequestContext().getUser();
    	
		// Test NLT
		System.out.println(NLT.get("exception.contactAdmin"));
		System.out.println(NLT.get("a.b.c.d"));
		System.out.println(NLT.get("exception.notAuthorized.message", "Hi!"));
		System.out.println(NLT.get("x.y.z", "Hi!"));	
    }

    public void updateEmployee (Employee employee) {
    	this.employees.put(employee.getKey(),employee);
    }

    public void deleteEmployee (Integer key) {
    	this.employees.remove(key);
    }

    public void deleteEmployee (Employee employee) {
        deleteEmployee(employee.getKey());
    }
}
