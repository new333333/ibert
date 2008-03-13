package com.sitescape.team.search;

import static com.sitescape.team.module.shared.EntityIndexUtils.CREATORID_FIELD;
import static com.sitescape.team.module.shared.EntityIndexUtils.ENTRY_ANCESTRY;
import static com.sitescape.team.module.shared.EntityIndexUtils.ENTRY_TYPE_FIELD;
import static com.sitescape.team.module.shared.EntityIndexUtils.FAMILY_FIELD;
import static com.sitescape.team.module.shared.EntityIndexUtils.FAMILY_FIELD_TASK;
import static com.sitescape.team.module.shared.EntityIndexUtils.MODIFICATION_DATE_FIELD;
import static com.sitescape.team.search.BasicIndexUtils.DOC_TYPE_ENTRY;
import static com.sitescape.team.search.BasicIndexUtils.DOC_TYPE_FIELD;
import static com.sitescape.team.search.Restrictions.eq;
import static com.sitescape.team.search.Restrictions.in;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.util.AllModulesInjected;

public class SearchUtils {

	public static Criteria tasksForUser(Long userId)
	{
		Criteria crit = new Criteria();
		crit.add(eq(FAMILY_FIELD,FAMILY_FIELD_TASK))
			.add(eq(DOC_TYPE_FIELD, DOC_TYPE_ENTRY))
			.add(eq(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME,userId.toString()));
		return crit;
	}
	
	public static Criteria entriesForUser(Long userId)
	{
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {EntityIndexUtils.ENTRY_TYPE_ENTRY, EntityIndexUtils.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {BasicIndexUtils.DOC_TYPE_ENTRY}))
			.add(eq(CREATORID_FIELD,userId.toString()));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}
	
	public static Criteria entriesForTrackedPlaces(AllModulesInjected bs, Binder userWorkspace)
	{
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {EntityIndexUtils.ENTRY_TYPE_ENTRY, 
				EntityIndexUtils.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {BasicIndexUtils.DOC_TYPE_ENTRY}))
			.add(in(ENTRY_ANCESTRY, getTrackedPlacesIds(bs, userWorkspace)));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}
	
	public static Criteria entriesForTrackedPeople(AllModulesInjected bs, Binder userWorkspace)
	{
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {EntityIndexUtils.ENTRY_TYPE_ENTRY, 
				EntityIndexUtils.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {BasicIndexUtils.DOC_TYPE_ENTRY}))
			.add(in(CREATORID_FIELD, getTrackedPeopleIds(bs, userWorkspace)));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}

	private static List<String> getTrackedPlacesIds(AllModulesInjected bs, Binder userWorkspace) {
		List<String> sIdList = new ArrayList<String>();
		UserProperties userForumProperties = bs.getProfileModule().getUserProperties(userWorkspace.getOwnerId(), userWorkspace.getId());
		Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
		if (relevanceMap != null) {
			List<Long> idList = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_BINDERS);
			if (idList != null) {
				for (Long id: idList) {
					sIdList.add(String.valueOf(id));
				}
			}
		}
		return sIdList;
	}

	private static List<String> getTrackedPeopleIds(AllModulesInjected bs, Binder binder) {
		List<String> sIdList = new ArrayList<String>();
		UserProperties userForumProperties = bs.getProfileModule().getUserProperties(binder.getOwnerId(), binder.getId());
		Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
		if (relevanceMap != null) {
			List trackedPeople = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_PEOPLE);
			if (trackedPeople != null) {
				List<Long> idList = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_BINDERS);
				if (idList != null) {
					for (Long id: idList) {
						sIdList.add(String.valueOf(id));
					}
				}
			}
		}
		return sIdList;
	}
}
