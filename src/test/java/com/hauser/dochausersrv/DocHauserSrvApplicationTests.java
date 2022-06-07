package com.hauser.dochausersrv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hauser.dochausersrv.model.SearchAggregation;
import com.hauser.dochausersrv.model.SearchMode;
import com.hauser.dochausersrv.model.SearchDocRequest;
import com.hauser.dochausersrv.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("dev")
class DocHauserSrvApplicationTests {

	@Autowired
	private MockMvc mockMvc;


	public static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testBasicSearch() throws Exception {

		String[] searchTerms = {"XXXLutz"};
		SearchDocRequest req = new SearchDocRequest(SearchAggregation.AND, 0, SearchMode.EXACT, null, null, searchTerms, null);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
				"/dochausersrv/search")
				.content(asJsonString(req))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}


	@Test
	public void testAddTagToDocument() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put(
						"/dochausersrv/document/446f1f0-a446-11ec-befa-0242ac140002/somebunny2")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		System.out.println(result.getResponse().getContentAsString());

	}

	@Test
	public void testRemoveTagFromDocument() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
						"/dochausersrv/document/446f1f0-a446-11ec-befa-0242ac140002/somebunny2")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		System.out.println(result.getResponse().getContentAsString());

	}

	@Test
	void testTagRepository() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/dochausersrv/tag")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void testTagAdd() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put(
						"/dochausersrv/tag/somebunny")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		System.out.println(result.getResponse().getContentAsString());

	}

	@Test
	public void testTagRemove() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(
						"/dochausersrv/tag/somebunny")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		System.out.println(result.getResponse().getContentAsString());

	}

	@Test
	public void testSetTags() throws Exception {

		String[] arr = { "Test", "Another", "AThird" };

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put(
						"/dochauser/tag")
				.content(asJsonString(Arrays.stream(arr).map(Tag::new).toArray(Tag[]::new)))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		System.out.println(result.getResponse().getContentAsString());

	}


}
