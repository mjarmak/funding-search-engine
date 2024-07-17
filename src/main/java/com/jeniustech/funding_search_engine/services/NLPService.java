package com.jeniustech.funding_search_engine.services;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class NLPService {
    private final TokenizerME tokenizer;
    private final POSTaggerME posTagger;
    private final NameFinderME nameFinder;
    private final DictionaryLemmatizer lemmatizer;
    private final Set<String> stopWords = new HashSet<>();

    public NLPService() throws IOException {
        InputStream tokenModelIn = getClass().getResourceAsStream("/nlp/en-token.bin");
        InputStream posModelIn = getClass().getResourceAsStream("/nlp/en-pos-maxent.bin");
        InputStream nameModelIn = getClass().getResourceAsStream("/nlp/en-ner-person.bin");

        if (tokenModelIn == null || posModelIn == null || nameModelIn == null) {
            throw new IOException("Failed to load NLP models");
        }

        TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
        POSModel posModel = new POSModel(posModelIn);
        TokenNameFinderModel nameModel = new TokenNameFinderModel(nameModelIn);


        tokenizer = new TokenizerME(tokenModel);
        posTagger = new POSTaggerME(posModel);
        nameFinder = new NameFinderME(nameModel);


        lemmatizer = getLemmatizer();

        loadStopWords();
    }

    private DictionaryLemmatizer getLemmatizer() throws IOException {
        InputStream dictLemmatizer = getClass().getResourceAsStream("/nlp/en-lemmatizer.dict");
        assert dictLemmatizer != null;
        return new DictionaryLemmatizer(dictLemmatizer);
    }

    private void loadStopWords() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/nlp/stopwords_en.txt");) {
            assert is != null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is));
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    stopWords.add(line.trim().toLowerCase());
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed to load stopwords", e);
        }
    }

    public List<String> getKeywords(String text) throws IOException {
        text = text.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();

        String[] tokens = tokenizer.tokenize(text);
        String[] posTags = posTagger.tag(tokens);
        Span[] nameSpans = nameFinder.find(tokens);

        String[] lemmas = lemmatizer.lemmatize(tokens, posTags);

        List<String> keywords = new ArrayList<>();
        for (int i = 0; i < lemmas.length; i++) {
            if (posTags[i].startsWith("NN")) {
                keywords.add(lemmas[i]);
            }
        }

        for (Span span : nameSpans) {
            keywords.addAll(Arrays.asList(tokens).subList(span.getStart(), span.getEnd()));
        }

        keywords.removeIf(word -> word.equals("O"));

        Map<String, Integer> wordFreq = new HashMap<>();
        for (String keyword : keywords) {
            wordFreq.put(keyword, wordFreq.getOrDefault(keyword, 0) + 1);
        }

        // Optional: Filter out stopwords
        wordFreq.keySet().removeAll(stopWords);

        // Sort words by frequency
        List<Map.Entry<String, Integer>> sortedWordFreq = new ArrayList<>(wordFreq.entrySet());
        sortedWordFreq.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<String> topKeywords = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedWordFreq) {
            topKeywords.add(entry.getKey());
        }
        return topKeywords.subList(0, Math.min(topKeywords.size(), 20));
    }

}
