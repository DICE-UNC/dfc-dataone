package org.irods.jargon.dataone.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.Log;
import org.dataone.service.types.v1.LogEntry;
import org.dataone.service.types.v1.NodeReference;
import org.dataone.service.types.v1.Subject;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.RuleProcessingAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.domain.ObjStat;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.rule.IRODSRuleExecResult;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.RestConfiguration;
import org.irods.jargon.dataone.id.UniqueIdAOHandleImpl;
import org.irods.jargon.dataone.utils.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.SearchHit;

public class EventLogAOElasticSearchImpl implements EventLogAO {
	
		// Hard-code subject for now
		private final String SUBJECT_USER = "dataone";
		final IRODSAccessObjectFactory irodsAccessObjectFactory;
		final RestConfiguration restConfiguration;
		
		private Logger logger = LoggerFactory.getLogger(this.getClass());
		
		public EventLogAOElasticSearchImpl(IRODSAccessObjectFactory irodsAccessObjectFactory,
				RestConfiguration restConfiguration) {
	    	
			this.irodsAccessObjectFactory = irodsAccessObjectFactory;
			this.restConfiguration = restConfiguration;
		}
		
		
		// retrieve list of logEntry (events)
		@SuppressWarnings("resource")
		// log?[fromDate={fromDate}][&toDate={toDate}][&event={event}][&pidFilter={pidFilter}][&start={start}][&count={count}]
		// TODO: The response MUST contain only records for which the requester has permission to read. This is
		// always okay because access is anonymous and read only?
		@Override
		public Log getLogs(Date fromDate, Date toDate, EventsEnum event, String pidFilter, int startIdx, int count)
			throws NoNodeAvailableException {
			
			if (fromDate != null) {
				logger.info("getLogs: fromDate= {}", fromDate.toString());
			}
			if (toDate != null) {
				logger.info("getLogs: toDate={}", toDate.toString());
			}
			if (event != null) {
				logger.info("getLogs: event={}", event.name());
			}
			if (pidFilter != null) {
				logger.info("getLogs: pidFilter={}", pidFilter);
			}
			logger.info("getLogs: startIdx={}, count={}", startIdx, count);
			
			// ignoring pidFilter for now
			
			boolean datesExist = true;
			if (fromDate == null && toDate == null) {
				datesExist = false;
			}
			
			// restrict search to DataONE exposed data objects
			// get this list from the Handle server
			// should return something like this:
			// 10/9/15 - worked with how to change databook rules so messages only get
			// created for objects under a certain dir: i.e. /eesZone/home/nexrad/NEXRAD_LEVEL_2/*.nc
			// so just assume we can query all elastic search messages, since they are only created
			// for the nexrad data
			String uriRegex = null;
			/* so don't need this anymore
			try {
				// "\"/dfc3/home/rods/fabfile.py2\".*|\"/dfc3/home/rods/fabfile.py3\".*"
				uriRegex = getDataOneDataObjectsRegex();
			} catch (JargonException e) {
				logger.error("error getting DataOne uids and converting to regex");
				throw new NoNodeAvailableException(e.getMessage());
			} */
			
			// maybe implement spring bean for these settings?
			// get elasticsearch properties
			PropertiesLoader loader = new PropertiesLoader();
			String elasticsearchDNS = loader.getProperty("irods.dataone.events.elasticsearch.dns");
			int elasticsearchport = Integer.parseInt(loader.getProperty("irods.dataone.events.elasticsearch.port"));
			String searchIndex = loader.getProperty("irods.dataone.events.elasticsearch.searchindex");
			String searchType = loader.getProperty("irods.dataone.events.elasticsearch.searchtype");
			String clusterName = loader.getProperty("irods.dataone.events.elasticsearch.cluster.name");
			String rangeField = "created";

			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
					.must(QueryBuilders.matchQuery("type", "databook.persistence.rule.rdf.ruleset.Access"));
				  //.must(QueryBuilders.matchQuery("userEntityLink", userEntityLink));
			if (event  != null) {
				boolQuery.must(QueryBuilders.matchQuery("title", event.getDatabookEvent()));
			}
			// skip any events of no interest to DataONE
			boolQuery.mustNot(QueryBuilders.matchQuery("title", "metadata add"));
			
			if (uriRegex != null && !uriRegex.isEmpty()) {
				boolQuery.must(QueryBuilders.regexpQuery("uri", uriRegex));
			}

			// Note that date time precision is limited to one millisecond.
			// TODO: If no timezone information is provided UTC will be assumed.
			RangeFilterBuilder filterBuilder = FilterBuilders
				.rangeFilter(rangeField);
			if (datesExist) {
				if (fromDate != null) {
					filterBuilder
						.from(fromDate.getTime())
						//.from(fromDate.getTime()/1000)
						.includeLower(true);
				}
				else {
					filterBuilder
					.from(0)
					.includeLower(true);
				}
				if (toDate != null) {
					filterBuilder
					.to(toDate.getTime())
					.includeUpper(false);
				}
				else {
					filterBuilder
					.to(System.currentTimeMillis())
					.includeUpper(false);
				}
			}

			logger.info("creating elastic search transport client: dns={}, port={}", elasticsearchDNS, elasticsearchport);
			Settings settings = ImmutableSettings.settingsBuilder()  //TODO: put this in config file
			        .put("cluster.name", clusterName).build();
			Client client = null;
			if(elasticsearchDNS != null && elasticsearchDNS.length() > 0) {
				client = new TransportClient(settings)
	        		.addTransportAddress(new InetSocketTransportAddress(elasticsearchDNS, elasticsearchport));
			}
			else {
				client = new TransportClient(settings)
        		.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
			}
	
			SearchRequestBuilder srBuilder = client.prepareSearch(searchIndex)
					.setTypes(searchType)
					//.setSearchType(SearchType.DEFAULT)
					.setQuery(boolQuery)
					//.setPostFilter(filterBuilder)
					.setFrom(startIdx).setSize(startIdx + count);
			if (datesExist) {
				srBuilder.setPostFilter(filterBuilder);
			}
			logger.info("getLogs: built search request: {}", srBuilder.toString());
			
			SearchResponse response = srBuilder.execute().actionGet();
			logger.info("getLogs: got search response: {}", response.toString());
			
			SearchHit[] searchHits = response.getHits().getHits();
			Log log = new Log();
			if (searchHits.length > 0) {
				// now put data and put in Log object
				try {
					log = copyHitsToLog(startIdx, count, response.getHits().getTotalHits(), searchHits);
				} catch (JargonException e) {
					client.close();
					logger.error("error copying elastic search hits into Log entries");
					throw new NoNodeAvailableException(e.getMessage());
				}
			}
			client.close();

			return log;
		}
		
		
		private Log copyHitsToLog(int start, int count, long totalHits, SearchHit[] searchHits) throws JargonException {
			
			int skipped = 0;
			Log log = new Log();
			// load properties for some log entries
			PropertiesLoader props = new PropertiesLoader();

			// go through search hits and populate log entries
			if (count > searchHits.length) count = searchHits.length;
			for (int c = 0; c < count; c++) {
				LogEntry logEntry = new LogEntry();
				// DataONE's Mark Servilla said this entry id should be an object id.
				// now set below, near setIdentifier
				//logEntry.setEntryId(String.valueOf(start++));
				
				Map<String, Object> hit = searchHits[c].sourceAsMap();
				// If this is not a databook event that we recognize, just skip this record
				String eventTitle = (String)hit.get("title");
				if ( EventsEnum.valueOfFromDatabook(eventTitle) == EventsEnum.FAILED) {
					skipped++;
					logger.info("Unrecognized event '{}' when parsing databook events - event skipped", eventTitle);
					continue;
				}

				Event event = EventsEnum.valueOfFromDatabook((String)hit.get("title")).getDataoneEvent();
				logEntry.setEvent(event);
				Date createdDate = new Date((Long)hit.get("created"));
				logEntry.setDateLogged(createdDate);

				ArrayList<Object> linkingDataEntity = (ArrayList<Object>)hit.get("linkingDataEntity");
				Map<String, Object> linkingDataEntity0 = (Map<String, Object>)linkingDataEntity.get(0);
				Map<String, Object> dataEntity = (Map<String, Object>)linkingDataEntity0.get("dataEntity");
			
				String objectUri = new String((String)dataEntity.get("uri"));
				int end = objectUri.indexOf('@');
				String objectPath = objectUri.substring(0, end);
				
				// check to make sure we can find this data object
				// if not just abandon this log entry
				String objectId = null;
				try {
					objectId = getDataObjectEntryId(objectPath);
				} catch (FileNotFoundException fnf) {
					skipped++;
					logger.info("Cannot find dataObject id for : {} - may have been removed - event skipped", objectPath);
					continue;
				}

				logger.info("found data object id: {}",objectId);
				logEntry.setEntryId(objectId);
				
				// check to make sure we can find this data object in handle server
				// if not just abandon this log entry
				Identifier identifier = getDataObjectIdentifier(objectPath);
				logEntry.setIdentifier(identifier);
				NodeReference nodeReference = new NodeReference();
				String nodeIdentifierProp = props.getProperty("irods.dataone.identifier");
				if (nodeIdentifierProp != null) {
					nodeReference.setValue(nodeIdentifierProp);
					logEntry.setNodeIdentifier(nodeReference);
				}
				
				// ipAddress? not included in elastic search response
				
				// userAgent not set

				Subject subject = new Subject();
				String subjectString = new String();
				subjectString += props.getProperty("irods.dataone.subject-string");
				subject.setValue(subjectString);
				logEntry.setSubject(subject);	
				
				logger.info("adding new log entry: {}", logEntry.getEntryId());
				logger.info("  date logged: {}", logEntry.getDateLogged());
				logger.info("  event: {}", logEntry.getEvent());
				logger.info("  object identifier: {}", logEntry.getIdentifier().getValue());
				logger.info("  node identifier: {}", logEntry.getNodeIdentifier().getValue());
				logger.info("  subject: {}", logEntry.getSubject().getValue());
				log.addLogEntry(logEntry);
			}
			
			// finally - set header values
			int total = 0;
			log.setStart(start);
			log.setCount(searchHits.length - skipped);
			if ( totalHits > (long)Integer.MAX_VALUE ) {
				total = Integer.MAX_VALUE - skipped;
			}
			else {
			    total = (int)totalHits - skipped;
			}
			log.setTotal(total);

			return log;
		}


		private String getDataOneDataObjectsRegex() throws JargonException {
			StringBuilder regexBuilder = new StringBuilder();
			
			UniqueIdAOHandleImpl handleImpl = new UniqueIdAOHandleImpl(restConfiguration, irodsAccessObjectFactory);
			List<Identifier> uids = handleImpl.getListOfDataoneExposedIdentifiers();
			
			for (Identifier uid : uids) {
				DataObject dataObject = handleImpl.getDataObjectFromIdentifier(uid);
				if (dataObject != null) {
					if (regexBuilder.length() > 0) {
						regexBuilder.append("|");
					}
					regexBuilder.append("\"");
					regexBuilder.append(dataObject.getAbsolutePath());
					regexBuilder.append("\".*");
				}
				else {
					logger.warn("iRODS data object with uid: {} not found " +
							 "- expecting that this is a DataOne exposed object");
				}
				logger.info("regex={}", regexBuilder.toString());
			}

			return regexBuilder.toString();
		}


		// execute rule to add event to databook event log
		// sendAccess("synch_failure", user name, data object uri, object type, timestamp in seconds, short description);
		@Override
		public void recordEvent(Event event, Identifier id, String description) 
									throws InvalidArgumentException, JargonException, ServiceFailure {
			
			// check for valid input parameters
			if (event == null) {
				throw new InvalidArgumentException("invalid Event parameter");
			}
			if (id == null) {
				throw new InvalidArgumentException("invalid data object identifier");
			}
			
			EventsEnum e = EventsEnum.valueOfFromDataOne(event);
			String databookEvent = e.getDatabookEvent();
			//Long timeNow = new Long(java.lang.System.currentTimeMillis()/1000);
			Long timeNow = new Long(java.lang.System.currentTimeMillis());
			String timeNowStr = timeNow.toString();
			
			UniqueIdAOHandleImpl handleImpl = new UniqueIdAOHandleImpl(restConfiguration, irodsAccessObjectFactory);
			DataObject dataObject = handleImpl.getDataObjectFromIdentifier(id);
			// going to use dataObject path for description - as suggested by Hao 5/1/15
			// this is required in order to be able to query event logs by path later
			String pathDescription = dataObject.getAbsolutePath();
			int dataObjectId = dataObject.getId();
			String dataObjIdStr = new Integer(dataObjectId).toString();
//			long dataObjectId = handleImpl.getDataObjectIdFromDataOneIdentifier(id);
//			String dataObjIdStr = new Long(dataObjectId).toString(); 
				
			IRODSAccount irodsAccount = RestAuthUtils
				.getIRODSAccountFromBasicAuthValues(this.restConfiguration);
			RuleProcessingAO ruleProcessingAO = irodsAccessObjectFactory
				.getRuleProcessingAO(irodsAccount);
			IRODSFile irodsFile = irodsAccessObjectFactory
					.getIRODSFileFactory(irodsAccount).instanceIRODSFile(pathDescription);
			Long createTime = new Long(irodsFile.lastModified());
			String createTimeStr = createTime.toString();
			
			StringBuilder sb = new StringBuilder();
			sb.append("addEventRule {\n");
			sb.append(" sendAccess(\"");
			sb.append(databookEvent);
			sb.append("\", \"");
			sb.append(SUBJECT_USER);
			sb.append("\", \"");
			sb.append(pathDescription);
			sb.append("@");
			sb.append(createTimeStr);
			sb.append("\", \"");
			sb.append("DataObject");
			sb.append("\", \"");
			sb.append(timeNowStr);
			
			if (description != null) {
				sb.append("\", \"");
				sb.append(description);
			}
			sb.append("\");\n}\n");
			sb.append("OUTPUT ruleExecOut");
			String ruleString = sb.toString();

			IRODSRuleExecResult result = ruleProcessingAO.executeRule(ruleString);
		}
		
		private String getDataObjectEntryId(String dataObjectPath) throws JargonException {
			String dataObjectId = null;
			
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
			
			CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO =
					irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount);
			logger.info("getting object records associated with path: {}", dataObjectPath);
			ObjStat objStat = collectionAndDataObjectListAndSearchAO
					.retrieveObjectStatForPath(dataObjectPath);
			int id = objStat.getDataId();
			dataObjectId = String.valueOf(id);	
			
			return dataObjectId;		
		}
		
		private Identifier getDataObjectIdentifier(String dataObjectPath) throws JargonException {
			
			Identifier identifier = null;
			
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(restConfiguration);
	
			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			UniqueIdAOHandleImpl uniqueIdAOHandleImpl = new UniqueIdAOHandleImpl(restConfiguration,
																				 irodsAccessObjectFactory);		
			identifier = uniqueIdAOHandleImpl.getIdentifierFromDataObject(dataObjectAO.findByAbsolutePath(dataObjectPath));
			
			return identifier;
		}

}
