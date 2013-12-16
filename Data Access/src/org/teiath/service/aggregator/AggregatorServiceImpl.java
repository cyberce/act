package org.teiath.service.aggregator;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.teiath.data.dao.FeedDAO;
import org.teiath.data.dao.FeedDataDAO;
import org.teiath.data.dao.FeedJobDAO;
import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.data.domain.aggregator.FeedJob;
import org.teiath.data.domain.aggregator.FeedType;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.sys.SysParameterService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Service("aggregatorService")
@Transactional
public class AggregatorServiceImpl
		implements AggregatorService {

	@Autowired
	private FeedDAO feedDAO;
	@Autowired
	private FeedJobDAO feedJobDAO;
	@Autowired
	private FeedDataDAO feedDataDAO;
	@Autowired
	private SysParameterService sysParameterService;

	@Override
	public Collection<Feed> fetchActiveFeeds() {
		return null;
	}

	@Override
	public void run(Timestamp timestamp) {
			try {
				if (sysParameterService.fetchSystemParameters().isAggregatorEnabled()) {
					FeedJob lastestJob = feedJobDAO.findLatestJob();

					if (lastestJob == null || lastestJob.getDate().before(timestamp)) {
						System.out.println("aggregator runned");
						FeedJob feedJob = new FeedJob();
						feedJob.setDate(new Timestamp(new Date().getTime()));
						feedJobDAO.save(feedJob);

						boolean feedDataSaved = false;
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						FeedData feedData;
						Collection<Feed> feeds = feedDAO.findActiveFeeds();
						for (Feed feed : feeds) {
							try {
								if (feed.getFeedType().getId() == FeedType.WEB_SERVICE) {
									feedDataSaved = false;
									HttpHeaders headers = new HttpHeaders();
									headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
									HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
									RestTemplate template = new RestTemplate();

									ObjectMapper mapper = new ObjectMapper();
									JsonFactory factory = mapper.getJsonFactory();
									JsonParser jsonParser;
									ResponseEntity<String> entity;

									entity = template.exchange(feed.getUrl().startsWith("http")? feed.getUrl(): "http://" + feed.getUrl(), HttpMethod.GET,  requestEntity, String.class);
									jsonParser = factory.createJsonParser(entity.getBody());

									JsonToken current = jsonParser.nextToken();

									if (current != JsonToken.START_OBJECT) {
										System.out.println("Error: root should be object: quiting.");
									} else {
										while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
											String fieldName = jsonParser.getCurrentName();

											current = jsonParser.nextToken();
											if (fieldName.equals("data")) {
												if (current == JsonToken.START_ARRAY) {
													while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
														JsonNode node = jsonParser.readValueAsTree();
														try {
															Date publishDate = sdf.parse(node.get("date_created").asText().trim());

															if (lastestJob == null || publishDate.after(lastestJob.getDate())) {
																feedData = new FeedData();
																feedData.setTitle(node.get("title").asText().trim());
																feedData.setDescription("");
																feedData.setUrl(node.get("url").asText().trim());
																feedData.setPublicationDate(new Timestamp(publishDate.getTime()));
																feedData.setStatus(1);
																feedData.setFeed(feed);
																feedData.setInteresting(FeedData.NEW);

																feedDataDAO.save(feedData);

																feed.getFeedData().add(feedData);
																feed.getFeedJobs().add(feedJob);
																feedDataSaved = true;
															}
														} catch (ParseException e) {
															e.printStackTrace();
														}
													}
												} else {
													jsonParser.skipChildren();
												}
											} else {
												jsonParser.skipChildren();
											}
										}

									}

									if (feedDataSaved) {
										feedDAO.save(feed);
									}

								} else {
									try {
										URL feedSource = new URL(feed.getUrl().startsWith("http")? feed.getUrl(): "http://" + feed.getUrl());
										SyndFeedInput input = new SyndFeedInput();
										SyndFeed syndFeed = input.build(new XmlReader(feedSource));

										SyndEntryImpl entry;
										for (int i = 0, j = syndFeed.getEntries().size(); i < j; i++) {
											entry = (SyndEntryImpl) syndFeed.getEntries().get(i);

											if (lastestJob == null || entry.getPublishedDate().after(lastestJob.getDate())) {
												feedData = new FeedData();
												feedData.setTitle(entry.getTitle().trim());
												feedData.setDescription(entry.getDescription().getValue().trim());
												feedData.setUrl(entry.getLink().trim());
												feedData.setPublicationDate(new Timestamp(entry.getPublishedDate().getTime()));
												feedData.setStatus(1);
												feedData.setFeed(feed);
												feedData.setInteresting(FeedData.NEW);


												feedDataDAO.save(feedData);

												feed.getFeedData().add(feedData);
												feed.getFeedJobs().add(feedJob);

											}
										}

										feedDAO.save(feed);
									}
									catch (com.sun.syndication.io.ParsingFeedException e) {

									}
								}
							} catch (MalformedURLException e) {
								e.printStackTrace();
							} catch (FeedException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.out.println();
						}
					}
				}
			} catch (ServiceException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void updateFeed(Timestamp timestamp, Feed feed) {
		try {
			if (sysParameterService.fetchSystemParameters().isAggregatorEnabled()) {
				FeedJob lastestJob = feedJobDAO.findLatestJobByFeed(feed);

				if (lastestJob == null || lastestJob.getDate().before(timestamp)) {
					FeedJob feedJob = new FeedJob();
					feedJob.setDate(new Timestamp(new Date().getTime()));
					feedJobDAO.save(feedJob);

						try {
							if (feed.getFeedType().getId() == FeedType.WEB_SERVICE) {
								boolean feedDataSaved = false;
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								FeedData feedData;

								HttpHeaders headers = new HttpHeaders();
								headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
								HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
								RestTemplate template = new RestTemplate();

								ObjectMapper mapper = new ObjectMapper();
								JsonFactory factory = mapper.getJsonFactory();
								JsonParser jsonParser;
								ResponseEntity<String> entity;

								entity = template.exchange(feed.getUrl().startsWith("http")? feed.getUrl(): "http://" + feed.getUrl(), HttpMethod.GET,  requestEntity, String.class);
								jsonParser = factory.createJsonParser(entity.getBody());

								JsonToken current = jsonParser.nextToken();

								if (current != JsonToken.START_OBJECT) {
									System.out.println("Error: root should be object: quiting.");
								} else {
									while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
										String fieldName = jsonParser.getCurrentName();

										current = jsonParser.nextToken();
										if (fieldName.equals("data")) {
											if (current == JsonToken.START_ARRAY) {
												while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
													JsonNode node = jsonParser.readValueAsTree();
													try {
														Date publishDate = sdf.parse(node.get("date_created").asText().trim());

														if (lastestJob == null || publishDate.after(lastestJob.getDate())) {
															feedData = new FeedData();
															feedData.setTitle(node.get("title").asText().trim());
															feedData.setDescription("");
															feedData.setUrl(node.get("url").asText().trim());
															feedData.setPublicationDate(new Timestamp(publishDate.getTime()));
															feedData.setStatus(1);
															feedData.setFeed(feed);
															feedData.setInteresting(FeedData.NEW);

															try {
																feedDataDAO.save(feedData);

																feed.getFeedData().add(feedData);
																feed.getFeedJobs().add(feedJob);
																feedDataSaved = true;
															} catch (ConstraintViolationException cve) {

															}catch (org.springframework.dao.DataIntegrityViolationException e) {

															}

														}
													} catch (ParseException e) {
														e.printStackTrace();
													}
												}
											} else {
												jsonParser.skipChildren();
											}
										} else {
											jsonParser.skipChildren();
										}
									}

									if (feedDataSaved) {
										feedDAO.save(feed);
									}
								}

							} else {
								URL feedSource = new URL(feed.getUrl().startsWith("http")? feed.getUrl(): "http://" + feed.getUrl());
								SyndFeedInput input = new SyndFeedInput();
								SyndFeed syndFeed = input.build(new XmlReader(feedSource));

								SyndEntryImpl entry;
								FeedData feedData;
								for (int i = 0, j = syndFeed.getEntries().size(); i < j; i++) {
									entry = (SyndEntryImpl) syndFeed.getEntries().get(i);

									if (lastestJob == null || entry.getPublishedDate().after(lastestJob.getDate())) {
										feedData = new FeedData();
										feedData.setTitle(entry.getTitle().trim());
										feedData.setDescription(entry.getDescription().getValue().trim());
										feedData.setUrl(entry.getLink().trim());
										feedData.setPublicationDate(new Timestamp(entry.getPublishedDate().getTime()));
										feedData.setStatus(1);
										feedData.setFeed(feed);
										feedData.setInteresting(FeedData.NEW);

										try {
											feedDataDAO.save(feedData);

											feed.getFeedData().add(feedData);
											feed.getFeedJobs().add(feedJob);
										} catch (org.springframework.dao.DataIntegrityViolationException e) {

										}
									}
								}

								feedDAO.save(feed);
							}
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (FeedException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (org.hibernate.exception.ConstraintViolationException e) {

						}
						System.out.println();
				}
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
}