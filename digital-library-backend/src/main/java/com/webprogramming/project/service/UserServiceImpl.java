package com.webprogramming.project.service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.EmailValidator;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.webprogramming.project.config.JsonUtils;
import com.webprogramming.project.dto.SearchResponseDto;
import com.webprogramming.project.dto.SearchTermDto;
import com.webprogramming.project.model.EtdDto;
import com.webprogramming.project.model.User;
import com.webprogramming.project.repository.EtdRepository;
import com.webprogramming.project.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private final UserRepository userRepository;

	private final JavaMailSender javaMailSender;

	@Autowired
	private RestHighLevelClient client;

	@Autowired
	private ElasticsearchOperations elasticsearchOperations;

	@Autowired
	private final EtdRepository etdRepository;

	Map<String, String> otpStorage = new HashMap<>();

	public UserServiceImpl(UserRepository userRepository, JavaMailSender javaMailSender, EtdRepository etdRepository) {
		super();
		this.userRepository = userRepository;
		this.javaMailSender = javaMailSender;
		this.etdRepository = etdRepository;
	}

	@Override
	public String saveUser(User user) {
		// TODO Auto-generated method stub
		if (userRepository.findByEmail(user.getEmail()) != null)
			return "The email entered is already in use";
		else {
			user.setPassword(encodePassword(user.getPassword()));
			userRepository.save(user);
			return "User saved successfully";
		}
	}

	@Override
	public String login(User loginUser) {
		User user = userRepository.findAll().stream()
				.filter(existingUser -> loginUser.getEmail().equals(existingUser.getEmail())).findFirst().orElse(null);
		System.out.println(
				user.getEmail() + decodePassword(user.getPassword()) + loginUser.getEmail() + loginUser.getPassword());
		if (user != null && user.getStatus().equals("APPROVED")
				&& loginUser.getPassword().equals(decodePassword(user.getPassword()))) {
//			String token = jwtUtil.generateToken(user.getRole(), user.getId());
			System.out.println(user.getEmail() + user.getPassword());
			sendOtpToMail(loginUser.getEmail());
			return user.getRole();
		} else {
			return "Failed";
		}
	}

	@Override
	public User updateProfile(User user) {
		User findUser = userRepository.findByEmail(user.getEmail());
//		if (user.getAge())
//		findUser.setAge(user.getAge());
//		System.out.println(findUser.getAge() + findUser.getRole());
		if (user.getPassword() != null)
			System.out.println(user.getPassword());
		findUser.setPassword(encodePassword(user.getPassword()));
		if (user.getPhoneNumber() != null)
			findUser.setPhoneNumber(user.getPhoneNumber());
		return userRepository.save(findUser);
	}

	@Override
	public List<User> viewProfile(String email) {
		// TODO Auto-generated method stub
		return userRepository.findAll().stream().filter(user -> user.getEmail().equals(email))
				.collect(Collectors.toList());
	}

	private String encodePassword(String password) {
		return Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
	}

	private String decodePassword(String password) {
		return new String(Base64.getDecoder().decode(password), StandardCharsets.UTF_8);
	}

	public String generateOtp() {
		// Generate a random 6-digit OTP
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	public String sendOtpToMail(String email) {
		EmailValidator emailValidator = EmailValidator.getInstance();
		if (emailValidator.isValid(email.trim())) {
			System.out.println("Valid email address: " + email);
			String otp = generateOtp();
			createOtpMail(email, otp);
			return "sent";
		} else {
			System.out.println("Invalid email address: " + email);
			return "failed";
		}
	}

	public void createOtpMail(String recipientEmail, String otp) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(recipientEmail);
		mailMessage.setSubject("Your OTP Code");
		mailMessage.setText("Your OTP code is: " + otp);
		otpStorage.put(recipientEmail, otp);
		javaMailSender.send(mailMessage);
	}

	@Override
	public String verifyOtp(String email, String otp) {
		// TODO Auto-generated method stub
		if (otp == otpStorage.get(email)) {
			otpStorage.remove(email);
			return "OTP verified";
		} else
			return "Wrong OTP";
	}

	@Override
	public String indexDocuments() {
		String csvFilePath = "C:/Users/vanch/Downloads/metadata_abstract.csv"; // Replace with your CSV file path
		try (Reader reader = new FileReader(csvFilePath)) {
			CsvMapper csvMapper = new CsvMapper();
			CsvSchema schema = CsvSchema.emptySchema().withHeader();
			MappingIterator<Map<String, String>> mappingIterator = csvMapper.readerFor(Map.class).with(schema)
					.readValues(reader);
			int currentIndex = 0;
			while (mappingIterator.hasNext()) {
				Map<String, String> record = mappingIterator.next();
				record.put("pdf", String.valueOf(currentIndex));
				indexJSONToElasticsearch(record, "etd-500");
				currentIndex++;
				record.put("wikifier_terms", "test");
			}

			return "Data indexed successfully!";
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed to index data.";
		}
	}

	public void indexJSONToElasticsearch(Map<String, String> json, String indexName) {
		try {
			IndexRequest request = new IndexRequest(indexName).source(json);
			IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String searchIndex(String searchTerm) {
		SearchRequest searchRequest = new SearchRequest("etd-500");
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchQuery("text", searchTerm).fuzziness("AUTO"));
		sourceBuilder.size(100);

		// Add a suggestion for spell checking
		SuggestBuilder suggestBuilder = new SuggestBuilder();
		SuggestionBuilder<?> termSuggestionBuilder = SuggestBuilders.termSuggestion("text").text(searchTerm)
				.maxEdits(2);
		suggestBuilder.addSuggestion("suggest_text", termSuggestionBuilder);
		sourceBuilder.suggest(suggestBuilder);

		searchRequest.source(sourceBuilder);

		try {
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

			if (searchResponse.getHits().getTotalHits().value > 0) {
				List<SearchTermDto> searchTermsList = new ArrayList<>();
				List<String> spellCorrectors = new ArrayList<>();

				// Get the suggestions
				Suggest suggest = searchResponse.getSuggest();
				if (suggest != null) {
					TermSuggestion termSuggestion = suggest.getSuggestion("suggest_text");
					if (termSuggestion != null) {
						List<TermSuggestion.Entry> entries = termSuggestion.getEntries();
						for (TermSuggestion.Entry entry : entries) {
							List<TermSuggestion.Entry.Option> options = entry.getOptions();
							for (TermSuggestion.Entry.Option option : options) {
								String correctedTerm = option.getText().string();
								if (spellCorrectors.size() < 3) {
									spellCorrectors.add(correctedTerm);
								} else {
									break; // Stop adding suggestions once the limit is reached
								}
							}
						}
					}
				}

				for (SearchHit hit : searchResponse.getHits().getHits()) {
					SearchTermDto searchTermDto = new SearchTermDto();
					searchTermDto.setEtdId(hit.getSourceAsMap().get("etd_file_id").toString());
					searchTermDto.setTitle(hit.getSourceAsMap().get("title").toString());
					searchTermDto.setSpellCorrectors(spellCorrectors);
					searchTermsList.add(searchTermDto);
				}

				// Convert the list of SearchResponseDto to JSON
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonResponse = objectMapper.writeValueAsString(searchTermsList);

				return jsonResponse;
			} else {
				return "No documents found for the search term";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "Search failed";
		}
	}

	public Map<String, String> getWikifierTerms(String text) throws Exception {
		Map<String, String> wikifierTerms = new HashMap<>();
		text = URLEncoder.encode(text, StandardCharsets.UTF_8);
		String apiUrl = "http://www.wikifier.org/annotate-article?userKey=gfmmujhrbdcetdeaunefyrsjjbogri&text=" + text;

		// Build the request
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl))
				.header("Content-Type", "application/json").GET() // Use GET method
				.build();
		System.out.println(request);

		// Send the request and get the response
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());

		// Process the response
		if (response.statusCode() == 200) {
			// Parse the JSON response to extract Wikifier terms
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(response.body());

			// Extract titles and urls from the annotations array
			JsonNode annotations = jsonNode.get("annotations");
			if (annotations.isArray()) {
				// Limit the number of terms to a maximum of 20
				int maxTerms = Math.min(25, annotations.size());

				for (int i = 0; i < maxTerms; i++) {
					JsonNode annotation = annotations.get(i);
					String title = annotation.get("title").asText();
					String url = annotation.get("url").asText();
					wikifierTerms.put(title, url);
				}
			}

			return wikifierTerms;
		} else {
			throw new RuntimeException("Wikifier API request failed with status code: " + response.statusCode());
		}
	}

	@Override
	public String getMetaData(String etdId) {
		// TODO Auto-generated method stub
		SearchRequest searchRequest = new SearchRequest("etd-500");
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchQuery("etd_file_id", etdId));
		System.out.println(etdId);
		searchRequest.source(sourceBuilder);

		try {
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

			if (searchResponse.getHits().getTotalHits().value > 0) {
				SearchHit hit = searchResponse.getHits().getHits()[0]; // Assuming you want the first hit

				SearchResponseDto searchResponseDto = new SearchResponseDto();
				searchResponseDto.setEtdid(hit.getSourceAsMap().get("etd_file_id").toString());
				searchResponseDto.setAdvisor(hit.getSourceAsMap().get("advisor").toString());
				searchResponseDto.setAuthor(hit.getSourceAsMap().get("author").toString());
				searchResponseDto.setDegree(hit.getSourceAsMap().get("degree").toString());
				searchResponseDto.setProgram(hit.getSourceAsMap().get("program").toString());
				searchResponseDto.setTitle(hit.getSourceAsMap().get("title").toString());
				searchResponseDto.setUniversity(hit.getSourceAsMap().get("university").toString());
				searchResponseDto.setYear(hit.getSourceAsMap().get("year").toString());
				searchResponseDto.setAbstractText(hit.getSourceAsMap().get("text").toString());
				searchResponseDto.setPdf("C:/Users/vanch/Downloads/drive-download-20231106T004123Z-001/"
						+ hit.getSourceAsMap().get("etd_file_id").toString() + ".pdf");
				try {
					searchResponseDto.setWikifierTerms(getWikifierTerms(hit.getSourceAsMap().get("text").toString()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Convert the SearchResponseDto to JSON
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonResponse = objectMapper.writeValueAsString(searchResponseDto);

				return jsonResponse;
			} else {
				return "No record found";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "Search failed";
		}
	}

	@Override
	public String uploadDocument(EtdDto etdDto) {
		// TODO Auto-generated method stub
		etdDto.setPdf("pdf");
		etdRepository.save(etdDto);
		return "Document saved";
	}

	@Override
	public String chatbot(String question) throws IOException, InterruptedException {
		final String OPENAI_API_KEY = "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";
		final String API_URL = "https://api.openai.com/v1/engines/text-davinci-003/completions";

		// Make API request
		Map<String, Object> requestBody = Map.of("prompt", question, "max_tokens", 100);

		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL))
				.header("Content-Type", "application/json").header("Authorization", "Bearer " + OPENAI_API_KEY)
				.POST(HttpRequest.BodyPublishers.ofString(JsonUtils.toJson(requestBody))).build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		// Process the response
		if (response.statusCode() == 200) {
			String modelResponse = response.body();
			System.out.println("Chatbot Response: " + modelResponse);
			return modelResponse;
		} else {
			System.err.println("Error: " + response.statusCode());
			System.err.println(response.body());
			return "Error in Chatbot response";
		}
	}

	@Override
	public String searchUsingParameters(String key, String searchTerm, int range) {
		SearchRequest searchRequest = new SearchRequest("etd-500");
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchQuery("text", searchTerm).fuzziness("AUTO"));
		sourceBuilder.size(range); // Use 'range' as the size

		// Add a suggestion for spell checking
		SuggestBuilder suggestBuilder = new SuggestBuilder();
		SuggestionBuilder<?> termSuggestionBuilder = SuggestBuilders.termSuggestion("text").text(searchTerm)
				.maxEdits(2);
		suggestBuilder.addSuggestion("suggest_text", termSuggestionBuilder);
		sourceBuilder.suggest(suggestBuilder);

		searchRequest.source(sourceBuilder);

		try {
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

			if (searchResponse.getHits().getTotalHits().value > 0) {
				List<SearchTermDto> searchTermsList = new ArrayList<>();
				List<String> spellCorrectors = new ArrayList<>();

				// Get the suggestions
				Suggest suggest = searchResponse.getSuggest();
				if (suggest != null) {
					TermSuggestion termSuggestion = suggest.getSuggestion("suggest_text");
					if (termSuggestion != null) {
						List<TermSuggestion.Entry> entries = termSuggestion.getEntries();
						for (TermSuggestion.Entry entry : entries) {
							List<TermSuggestion.Entry.Option> options = entry.getOptions();
							for (TermSuggestion.Entry.Option option : options) {
								String correctedTerm = option.getText().string();
								if (spellCorrectors.size() < 3) {
									spellCorrectors.add(correctedTerm);
								} else {
									break; // Stop adding suggestions once the limit is reached
								}
							}
						}
					}
				}

				for (SearchHit hit : searchResponse.getHits().getHits()) {
					SearchTermDto searchTermDto = new SearchTermDto();
					searchTermDto.setEtdId(hit.getSourceAsMap().get("etd_file_id").toString());
					searchTermDto.setTitle(hit.getSourceAsMap().get("title").toString());
					searchTermDto.setSpellCorrectors(spellCorrectors);
					searchTermsList.add(searchTermDto);
				}

				// Convert the list of SearchResponseDto to JSON
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonResponse = objectMapper.writeValueAsString(searchTermsList);

				return jsonResponse;
			} else {
				return "No documents found for the search term";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "Search failed";
		}
	}

}
