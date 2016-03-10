package edu.cornell.cs.nlp.assignments;

import java.util.*;

import edu.cornell.cs.nlp.assignments.counting.Counter;
import edu.cornell.cs.nlp.assignments.counting.CounterMap;
import edu.cornell.cs.nlp.assignments.counting.Counters;
import edu.cornell.cs.nlp.util.CommandLineUtils;
import edu.cornell.cs.nlp.util.io.PennTreebankReader;
import edu.cornell.cs.nlp.util.ling.Tree;
import edu.cornell.cs.nlp.util.ling.Trees;
import edu.cornell.cs.nlp.util.log.ILogger;
import edu.cornell.cs.nlp.util.log.Log;
import edu.cornell.cs.nlp.util.log.LogLevel;
import edu.cornell.cs.nlp.util.log.Logger;
import edu.cornell.cs.nlp.util.log.LoggerFactory;

/**
 * @author Dan Klein, Berkeley
 */
public class POSTaggerTester {

	public static final ILogger	LOG			= LoggerFactory
			.create(POSTaggerTester.class);

	static final String			START_TAG	= "<S>";
	static final String			START_WORD	= "<S>";
	static final String			STOP_TAG	= "</S>";
	static final String			STOP_WORD	= "</S>";

	public static void main(String[] args) {

		////////////////////////////////////////////
		// A logging system you may use. You can choose to delete this code and
		// completely ignore it.
		////////////////////////////////////////////

		// Set the logger.

		LogLevel.setLogLevel(LogLevel.INFO);

		// Remove class prefix from log message.
		// Logger.setSkipPrefix(false);

		// Set the output stream for the default log. Defaulting to STDOUT to
		// avoid conflicts with System.out messages.
		Logger.DEFAULT_LOG = new Log(System.out);
		// Logger.DEFAULT_LOG = new Log(System.err);

		LOG.debug("DEBUG log message, current log level is %s",
				LOG.getLogLevel());
		LOG.dev("DEV log message, current log level is %s", LOG.getLogLevel());
		LOG.info("INFO log message, current log level is %s",
				LOG.getLogLevel());
		LOG.warn("WARN log message, current log level is %s",
				LOG.getLogLevel());
		LOG.error("ERROR log message, current log level is %s",
				LOG.getLogLevel());

		////////////////////////////////////////////
		// Logger set up and example code up to here.
		////////////////////////////////////////////

		// Parse command line flags and arguments
		final Map<String, String> argMap = CommandLineUtils
				.simpleCommandLineParser(args);

		// Set up default parameters and settings
		String basePath = ".";
		boolean verbose = false;
		boolean useValidation = true;

		// Update defaults using command line specifications

		// The path to the assignment data
		if (argMap.containsKey("-path")) {
			basePath = argMap.get("-path");
		}
		System.out.println("Using base path: " + basePath);

		// Whether to use the validation or test set
		if (argMap.containsKey("-test")) {
			final String testString = argMap.get("-test");
			if (testString.equalsIgnoreCase("test")) {
				useValidation = false;
			}
		}
		System.out.println(
				"Testing on: " + (useValidation ? "validation" : "test"));

		// Whether or not to print the individual errors.
		if (argMap.containsKey("-verbose")) {
			verbose = true;
		}

		// Read in data
		System.out.print("Loading training sentences...");
		final List<TaggedSentence> trainTaggedSentences = readTaggedSentences(
				basePath, 200, 2199);
		final Set<String> trainingVocabulary = extractVocabulary(
				trainTaggedSentences);
		System.out.println("done.");
		System.out.print("Loading validation sentences...");
		final List<TaggedSentence> validationTaggedSentences = readTaggedSentences(
				basePath, 2200, 2299);
		System.out.println("done.");
		System.out.print("Loading test sentences...");
		final List<TaggedSentence> testTaggedSentences = readTaggedSentences(
				basePath, 2300, 2399);
		System.out.println("done.");

		final LocalTrigramScorer localTrigramScorer;
		final TrellisDecoder<State> trellisDecoder;

		// Construct tagger components
		if (argMap.containsKey("-hmm")){
			localTrigramScorer = new HMMScorer(false);
		} else {
			localTrigramScorer = new MostFrequentTagScorer(false);
		}

		if (argMap.containsKey("-viterbi")){
			trellisDecoder = new ViterbiDecoder<State>();
		} else {
			trellisDecoder = new GreedyDecoder<State>();
		}


		// Train tagger
		final POSTagger posTagger = new POSTagger(localTrigramScorer,
				trellisDecoder);
		posTagger.train(trainTaggedSentences);
		posTagger.validate(validationTaggedSentences);

		// Evaluation set, use either test of validation (for dev)
		final List<TaggedSentence> evalTaggedSentences;
		if (useValidation) {
			evalTaggedSentences = validationTaggedSentences;
		} else {
			evalTaggedSentences = testTaggedSentences;
		}

		// Test tagger
		evaluateTagger(posTagger, evalTaggedSentences, trainingVocabulary,
				verbose);
	}

	// pretty-print a pair of taggings for a sentence, possibly suppressing the
	// tags which correctly match
	private static String alignedTaggings(List<String> words,
			List<String> goldTags, List<String> guessedTags,
			boolean suppressCorrectTags) {
		final StringBuilder goldSB = new StringBuilder("Gold Tags: ");
		final StringBuilder guessedSB = new StringBuilder("Guessed Tags: ");
		final StringBuilder wordSB = new StringBuilder("Words: ");
		for (int position = 0; position < words.size(); position++) {
			equalizeLengths(wordSB, goldSB, guessedSB);
			final String word = words.get(position);
			final String gold = goldTags.get(position);
			final String guessed = guessedTags.get(position);
			wordSB.append(word);
			if (position < words.size() - 1) {
				wordSB.append(' ');
			}
			final boolean correct = gold.equals(guessed);
			if (correct && suppressCorrectTags) {
				continue;
			}
			guessedSB.append(guessed);
			goldSB.append(gold);
		}
		return goldSB + "\n" + guessedSB + "\n" + wordSB;
	}

	private static void ensureLength(StringBuilder sb, int length) {
		while (sb.length() < length) {
			sb.append(' ');
		}
	}

	private static void equalizeLengths(StringBuilder sb1, StringBuilder sb2,
			StringBuilder sb3) {
		int maxLength = sb1.length();
		maxLength = Math.max(maxLength, sb2.length());
		maxLength = Math.max(maxLength, sb3.length());
		ensureLength(sb1, maxLength);
		ensureLength(sb2, maxLength);
		ensureLength(sb3, maxLength);
	}

	private static void evaluateTagger(POSTagger posTagger,
			List<TaggedSentence> taggedSentences,
			Set<String> trainingVocabulary, boolean verbose) {
		double numTags = 0.0;
		double numTagsCorrect = 0.0;
		double numUnknownWords = 0.0;
		double numUnknownWordsCorrect = 0.0;
		int numDecodingInversions = 0;
		for (final TaggedSentence taggedSentence : taggedSentences) {
			final List<String> words = taggedSentence.getWords();
			final List<String> goldTags = taggedSentence.getTags();
			final List<String> guessedTags = posTagger.tag(words);
			for (int position = 0; position < words.size() - 1; position++) {
				final String word = words.get(position);
				final String goldTag = goldTags.get(position);
				final String guessedTag = guessedTags.get(position);
				if (guessedTag.equals(goldTag)) {
					numTagsCorrect += 1.0;
				}
				numTags += 1.0;
				if (!trainingVocabulary.contains(word)) {
					if (guessedTag.equals(goldTag)) {
						numUnknownWordsCorrect += 1.0;
					}
					numUnknownWords += 1.0;
				}
			}
			final double scoreOfGoldTagging = posTagger
					.scoreTagging(taggedSentence);
			final double scoreOfGuessedTagging = posTagger
					.scoreTagging(new TaggedSentence(words, guessedTags));
			if (scoreOfGoldTagging > scoreOfGuessedTagging) {
				numDecodingInversions++;
				if (verbose) {
					System.out.println(
							"WARNING: Decoder suboptimality detected.  Gold tagging has higher score than guessed tagging.");
				}
			}
			if (verbose) {
				System.out.println(
						alignedTaggings(words, goldTags, guessedTags, true)
								+ "\n");
			}
		}
		System.out.println("Tag Accuracy: " + numTagsCorrect / numTags
				+ " (Unknown Accuracy: "
				+ numUnknownWordsCorrect / numUnknownWords
				+ ")  Decoder Suboptimalities Detected: "
				+ numDecodingInversions);
	}

	private static Set<String> extractVocabulary(
			List<TaggedSentence> taggedSentences) {
		final Set<String> vocabulary = new HashSet<String>();
		for (final TaggedSentence taggedSentence : taggedSentences) {
			final List<String> words = taggedSentence.getWords();
			vocabulary.addAll(words);
		}
		return vocabulary;
	}

	private static List<TaggedSentence> readTaggedSentences(String path,
			int low, int high) {
		final Collection<Tree<String>> trees = PennTreebankReader
				.readTrees(path, low, high);
		final List<TaggedSentence> taggedSentences = new ArrayList<TaggedSentence>();
		final Trees.TreeTransformer<String> treeTransformer = new Trees.EmptyNodeStripper();
		for (Tree<String> tree : trees) {
			tree = treeTransformer.transformTree(tree);
			final List<String> words = new BoundedList<String>(
					new ArrayList<String>(tree.getYield()), START_WORD,
					STOP_WORD);
			final List<String> tags = new BoundedList<String>(
					new ArrayList<String>(tree.getPreTerminalYield()),
					START_TAG, STOP_TAG);
			taggedSentences.add(new TaggedSentence(words, tags));
		}
		return taggedSentences;
	}

	static class GreedyDecoder<S> implements TrellisDecoder<S> {
		@Override
		public List<S> getBestPath(Trellis<S> trellis) {
			final List<S> states = new ArrayList<S>();
			S currentState = trellis.getStartState();
			states.add(currentState);
			while (!currentState.equals(trellis.getEndState())) {
				final Counter<S> transitions = trellis
						.getForwardTransitions(currentState);
				final S nextState = transitions.argMax();
				states.add(nextState);
				currentState = nextState;
			}
			return states;
		}
	}

	static class ViterbiDecoder<S> implements TrellisDecoder<S> {

		@Override
		public List<S> getBestPath(Trellis<S> trellis) {
			final boolean debug = false;
			final List<Counter<S>> states = new ArrayList<Counter<S>>();
			final List<HashMap<S, S>> backpointers= new ArrayList<HashMap<S, S>>();

			//First set of transitions is populated (counter is a map from S->Double
			S startstate = trellis.getStartState();
			S endstate = trellis.getEndState();

			Counter<S> s0;
			Counter<S> s1 = trellis.getForwardTransitions(startstate);
			HashMap<S, S> b1 = new HashMap<S, S>();

			states.add(s1);
			backpointers.add(b1);

			if (debug) {
				System.out.println("Start State: " + startstate.toString());
				System.out.println("End State: " + endstate.toString());
				System.out.println("Forward transitions: " + s1.toString());
			}


			for (S k : s1.keySet()) {
				b1.put(k, startstate);
			}

			boolean end = false;

			do {
				s0 = s1;
				s1 = new Counter<S>();
				b1 = new HashMap<S, S>();
				states.add(s1);
				backpointers.add(b1);

				for (S state : s0.keySet()) {
					Counter<S> f = trellis.getForwardTransitions(state);

					if (debug) {
						System.out.println("");
						System.out.println("");
						System.out.println(state);
						System.out.println(f);
						System.out.println("");
						System.out.println("");
					}

					for (S st : f.keySet()) {
						if (st.equals(endstate))
							end = true;
						Counter<S> bt = trellis.getBackwardTransitions(st);

						if (debug)
							System.out.println("Backward transitions for " + st + ": " + bt.toString());

						for (S k : bt.keySet()) {

							if (debug) {
								System.out.println(k);
								System.out.println(s0.getCount(k));
								System.out.println(f.getCount(st));
							}

							bt.setCount(k, s0.getCount(k) + f.getCount(st));
						}

						S most_likely = bt.argMax();
						Double score = bt.getCount(most_likely);
						s1.setCount(st, score);
						b1.put(st, most_likely);

						if (debug) {
							System.out.println(most_likely);
							System.out.println(score);
						}
					}
				}
			} while (!end);

			if (debug) {
				Integer j = 0;
				for (HashMap<S, S> hm : backpointers) {
					System.out.println("");
					System.out.println("Token " + j.toString() + ":");
					System.out.println(hm);
					j++;
				}
			}

			List<S> ret = new ArrayList<S>();
			S st = endstate;
			ret.add(0, st);

			if (debug)
				System.out.println(st);

			for (int i = backpointers.size() - 1; i >= 0; --i) {
				st = backpointers.get(i).get(st);
				if (debug)
					System.out.println(st);
				ret.add(0, st);
			}

			if (debug)
				System.out.println(ret);

			return ret;
		}

	}

	/**
	 * A LabeledLocalTrigramContext is a context plus the correct tag for that
	 * position -- basically a LabeledFeatureVector
	 */
	static class LabeledLocalTrigramContext extends LocalTrigramContext {
		String currentTag;

		public LabeledLocalTrigramContext(List<String> words, int position,
				String previousPreviousTag, String previousTag,
				String currentTag) {
			super(words, position, previousPreviousTag, previousTag);
			this.currentTag = currentTag;
		}

		public String getCurrentTag() {
			return currentTag;
		}

		@Override
		public String toString() {
			return "[" + getPreviousPreviousTag() + ", " + getPreviousTag()
					+ ", " + getCurrentWord() + "_" + getCurrentTag() + "]";
		}
	}

	/**
	 * A LocalTrigramContext is a position in a sentence, along with the
	 * previous
	 * two tags -- basically a FeatureVector.
	 */
	static class LocalTrigramContext {
		int				position;
		String			previousPreviousTag;
		String			previousTag;
		List<String>	words;

		public LocalTrigramContext(List<String> words, int position,
				String previousPreviousTag, String previousTag) {
			this.words = words;
			this.position = position;
			this.previousTag = previousTag;
			this.previousPreviousTag = previousPreviousTag;
		}

		public String getCurrentWord() {
			return words.get(position);
		}

		public int getPosition() {
			return position;
		}

		public String getPreviousPreviousTag() {
			return previousPreviousTag;
		}

		public String getPreviousTag() {
			return previousTag;
		}

		public List<String> getWords() {
			return words;
		}

		@Override
		public String toString() {
			return "[" + getPreviousPreviousTag() + ", " + getPreviousTag()
					+ ", " + getCurrentWord() + "]";
		}
	}

	/**
	 * LocalTrigramScorers assign scores to tags occuring in specific
	 * LocalTrigramContexts.
	 */
	static interface LocalTrigramScorer {
		/**
		 * The Counter returned should contain log probabilities, meaning if all
		 * values are exponentiated and summed, they should sum to one (if it's
		 * a
		 * single conditional pobability). For efficiency, the Counter can
		 * contain only the tags which occur in the given context
		 * with non-zero model probability.
		 */
		Counter<String> getLogScoreCounter(
				LocalTrigramContext localTrigramContext);

		void train(List<LabeledLocalTrigramContext> localTrigramContexts);

		void validate(List<LabeledLocalTrigramContext> localTrigramContexts);
	}

	/**
	 * The MostFrequentTagScorer gives each test word the tag it was seen with
	 * most often in training (or the tag with the most seen word types if the
	 * test word is unseen in training. This scorer actually does a little more
	 * than its name claims -- if constructed with restrictTrigrams = true, it
	 * will forbid illegal tag trigrams, otherwise it makes no use of tag
	 * history
	 * information whatsoever.
	 */
	static class HMMScorer implements LocalTrigramScorer {
		//TODO: Implement the actual HMM Scorer

		boolean						restrictTrigrams;									// if
																						// true,
																						// assign
																						// log
																						// score
																						// of
																						// Double.NEGATIVE_INFINITY
																						// to
																						// illegal
																						// tag
																						// trigrams.

		Set<String>					seenTagTrigrams	= new HashSet<String>();
		Counter<String>				unknownWordTags	= new Counter<String>();
		CounterMap<String, String>	wordsToTags		= new CounterMap<String, String>();

		public HMMScorer(boolean restrictTrigrams) {
			this.restrictTrigrams = restrictTrigrams;
		}

		private static String makeTrigramString(String previousPreviousTag,
				String previousTag, String currentTag) {
			return previousPreviousTag + " " + previousTag + " " + currentTag;
		}

		public int getHistorySize() {
			return 2;
		}

		@Override
		public Counter<String> getLogScoreCounter(
				LocalTrigramContext localTrigramContext) {
			final int position = localTrigramContext.getPosition();
			final String word = localTrigramContext.getWords().get(position);
			Counter<String> tagCounter = unknownWordTags;
			if (wordsToTags.keySet().contains(word)) {
				tagCounter = wordsToTags.getCounter(word);
			}
			final Set<String> allowedFollowingTags = allowedFollowingTags(
					tagCounter.keySet(),
					localTrigramContext.getPreviousPreviousTag(),
					localTrigramContext.getPreviousTag());
			final Counter<String> logScoreCounter = new Counter<String>();
			for (final String tag : tagCounter.keySet()) {
				final double logScore = Math.log(tagCounter.getCount(tag));
				if (!restrictTrigrams || allowedFollowingTags.isEmpty()
						|| allowedFollowingTags.contains(tag)) {
					logScoreCounter.setCount(tag, logScore);
				}
			}
			return logScoreCounter;
		}

		@Override
		public void train(
				List<LabeledLocalTrigramContext> labeledLocalTrigramContexts) {
			// collect word-tag counts
			for (final LabeledLocalTrigramContext labeledLocalTrigramContext : labeledLocalTrigramContexts) {
				final String word = labeledLocalTrigramContext.getCurrentWord();
				final String tag = labeledLocalTrigramContext.getCurrentTag();
				if (!wordsToTags.keySet().contains(word)) {
					// word is currently unknown, so tally its tag in the
					// unknown tag counter
					unknownWordTags.incrementCount(tag, 1.0);
				}
				wordsToTags.incrementCount(word, tag, 1.0);
				seenTagTrigrams.add(makeTrigramString(
						labeledLocalTrigramContext.getPreviousPreviousTag(),
						labeledLocalTrigramContext.getPreviousTag(),
						labeledLocalTrigramContext.getCurrentTag()));
			}
			wordsToTags = Counters.conditionalNormalize(wordsToTags);
			unknownWordTags = Counters.normalize(unknownWordTags);
		}

		@Override
		public void validate(
				List<LabeledLocalTrigramContext> labeledLocalTrigramContexts) {
			// no tuning for this dummy model!
		}

		private Set<String> allowedFollowingTags(Set<String> tags,
				String previousPreviousTag, String previousTag) {
			final Set<String> allowedTags = new HashSet<String>();
			for (final String tag : tags) {
				final String trigramString = makeTrigramString(
						previousPreviousTag, previousTag, tag);
				if (seenTagTrigrams.contains(trigramString)) {
					allowedTags.add(tag);
				}
			}
			return allowedTags;
		}
	}

	/**
	 * The MostFrequentTagScorer gives each test word the tag it was seen with
	 * most often in training (or the tag with the most seen word types if the
	 * test word is unseen in training. This scorer actually does a little more
	 * than its name claims -- if constructed with restrictTrigrams = true, it
	 * will forbid illegal tag trigrams, otherwise it makes no use of tag
	 * history
	 * information whatsoever.
	 */
	static class MostFrequentTagScorer implements LocalTrigramScorer {

		boolean						restrictTrigrams;									// if
																						// true,
																						// assign
																						// log
																						// score
																						// of
																						// Double.NEGATIVE_INFINITY
																						// to
																						// illegal
																						// tag
																						// trigrams.

		Set<String>					seenTagTrigrams	= new HashSet<String>();
		Counter<String>				unknownWordTags	= new Counter<String>();
		CounterMap<String, String>	wordsToTags		= new CounterMap<String, String>();

		public MostFrequentTagScorer(boolean restrictTrigrams) {
			this.restrictTrigrams = restrictTrigrams;
		}

		private static String makeTrigramString(String previousPreviousTag,
				String previousTag, String currentTag) {
			return previousPreviousTag + " " + previousTag + " " + currentTag;
		}

		public int getHistorySize() {
			return 2;
		}

		@Override
		public Counter<String> getLogScoreCounter(
				LocalTrigramContext localTrigramContext) {
			final int position = localTrigramContext.getPosition();
			final String word = localTrigramContext.getWords().get(position);
			Counter<String> tagCounter = unknownWordTags;
			if (wordsToTags.keySet().contains(word)) {
				tagCounter = wordsToTags.getCounter(word);
			}
			final Set<String> allowedFollowingTags = allowedFollowingTags(
					tagCounter.keySet(),
					localTrigramContext.getPreviousPreviousTag(),
					localTrigramContext.getPreviousTag());
			final Counter<String> logScoreCounter = new Counter<String>();
			for (final String tag : tagCounter.keySet()) {
				final double logScore = Math.log(tagCounter.getCount(tag));
				if (!restrictTrigrams || allowedFollowingTags.isEmpty()
						|| allowedFollowingTags.contains(tag)) {
					logScoreCounter.setCount(tag, logScore);
				}
			}
			return logScoreCounter;
		}

		@Override
		public void train(
				List<LabeledLocalTrigramContext> labeledLocalTrigramContexts) {
			// collect word-tag counts
			for (final LabeledLocalTrigramContext labeledLocalTrigramContext : labeledLocalTrigramContexts) {
				final String word = labeledLocalTrigramContext.getCurrentWord();
				final String tag = labeledLocalTrigramContext.getCurrentTag();
				if (!wordsToTags.keySet().contains(word)) {
					// word is currently unknown, so tally its tag in the
					// unknown tag counter
					unknownWordTags.incrementCount(tag, 1.0);
				}
				wordsToTags.incrementCount(word, tag, 1.0);
				seenTagTrigrams.add(makeTrigramString(
						labeledLocalTrigramContext.getPreviousPreviousTag(),
						labeledLocalTrigramContext.getPreviousTag(),
						labeledLocalTrigramContext.getCurrentTag()));
			}
			wordsToTags = Counters.conditionalNormalize(wordsToTags);
			unknownWordTags = Counters.normalize(unknownWordTags);
		}

		@Override
		public void validate(
				List<LabeledLocalTrigramContext> labeledLocalTrigramContexts) {
			// no tuning for this dummy model!
		}

		private Set<String> allowedFollowingTags(Set<String> tags,
				String previousPreviousTag, String previousTag) {
			final Set<String> allowedTags = new HashSet<String>();
			for (final String tag : tags) {
				final String trigramString = makeTrigramString(
						previousPreviousTag, previousTag, tag);
				if (seenTagTrigrams.contains(trigramString)) {
					allowedTags.add(tag);
				}
			}
			return allowedTags;
		}
	}

	static class POSTagger {

		LocalTrigramScorer		localTrigramScorer;
		TrellisDecoder<State>	trellisDecoder;

		public POSTagger(LocalTrigramScorer localTrigramScorer,
				TrellisDecoder<State> trellisDecoder) {
			this.localTrigramScorer = localTrigramScorer;
			this.trellisDecoder = trellisDecoder;
		}

		private static List<LabeledLocalTrigramContext> extractLabeledLocalTrigramContexts(
				List<TaggedSentence> taggedSentences) {
			final List<LabeledLocalTrigramContext> localTrigramContexts = new ArrayList<LabeledLocalTrigramContext>();
			for (final TaggedSentence taggedSentence : taggedSentences) {
				localTrigramContexts.addAll(
						extractLabeledLocalTrigramContexts(taggedSentence));
			}
			return localTrigramContexts;
		}

		private static List<LabeledLocalTrigramContext> extractLabeledLocalTrigramContexts(
				TaggedSentence taggedSentence) {
			final List<LabeledLocalTrigramContext> labeledLocalTrigramContexts = new ArrayList<LabeledLocalTrigramContext>();
			final List<String> words = new BoundedList<String>(
					taggedSentence.getWords(), START_WORD, STOP_WORD);
			final List<String> tags = new BoundedList<String>(
					taggedSentence.getTags(), START_TAG, STOP_TAG);
			for (int position = 0; position <= taggedSentence.size()
					+ 1; position++) {
				labeledLocalTrigramContexts.add(new LabeledLocalTrigramContext(
						words, position, tags.get(position - 2),
						tags.get(position - 1), tags.get(position)));
			}
			return labeledLocalTrigramContexts;
		}

		private static List<String> stripBoundaryTags(List<String> tags) {
			return tags.subList(2, tags.size() - 2);
		}

		/**
		 * Scores a tagging for a sentence. Note that a tag sequence not
		 * accepted
		 * by the markov process should receive a log score of
		 * Double.NEGATIVE_INFINITY.
		 */
		public double scoreTagging(TaggedSentence taggedSentence) {
			double logScore = 0.0;
			final List<LabeledLocalTrigramContext> labeledLocalTrigramContexts = extractLabeledLocalTrigramContexts(
					taggedSentence);
			for (final LabeledLocalTrigramContext labeledLocalTrigramContext : labeledLocalTrigramContexts) {
				final Counter<String> logScoreCounter = localTrigramScorer
						.getLogScoreCounter(labeledLocalTrigramContext);
				final String currentTag = labeledLocalTrigramContext
						.getCurrentTag();
				if (logScoreCounter.containsKey(currentTag)) {
					logScore += logScoreCounter.getCount(currentTag);
				} else {
					logScore += Double.NEGATIVE_INFINITY;
				}
			}
			return logScore;
		}

		// to tag a sentence: build its trellis and find a path through that
		// trellis
		public List<String> tag(List<String> sentence) {
			final Trellis<State> trellis = buildTrellis(sentence);
			final List<State> states = trellisDecoder.getBestPath(trellis);
			List<String> tags = State.toTagList(states);
			tags = stripBoundaryTags(tags);
			return tags;
		}

		// chop up the training instances into local contexts and pass them on
		// to the local scorer.
		public void train(List<TaggedSentence> taggedSentences) {
			localTrigramScorer
					.train(extractLabeledLocalTrigramContexts(taggedSentences));
		}

		// chop up the validation instances into local contexts and pass them on
		// to the local scorer.
		public void validate(List<TaggedSentence> taggedSentences) {
			localTrigramScorer.validate(
					extractLabeledLocalTrigramContexts(taggedSentences));
		}

		/**
		 * Builds a Trellis over a sentence, by starting at the state State, and
		 * advancing through all legal extensions of each state already in the
		 * trellis. You should not have to modify this code (or even read it,
		 * really).
		 */
		private Trellis<State> buildTrellis(List<String> sentence) {
			final Trellis<State> trellis = new Trellis<State>();
			trellis.setStartState(State.getStartState());
			final State stopState = State.getStopState(sentence.size() + 2);
			trellis.setStopState(stopState);
			Set<State> states = Collections.singleton(State.getStartState());
			for (int position = 0; position <= sentence.size()
					+ 1; position++) {
				final Set<State> nextStates = new HashSet<State>();
				for (final State state : states) {
					if (state.equals(stopState)) {
						continue;
					}
					final LocalTrigramContext localTrigramContext = new LocalTrigramContext(
							sentence, position, state.getPreviousPreviousTag(),
							state.getPreviousTag());
					final Counter<String> tagScores = localTrigramScorer
							.getLogScoreCounter(localTrigramContext);
					for (final String tag : tagScores.keySet()) {
						final double score = tagScores.getCount(tag);
						final State nextState = state.getNextState(tag);
						trellis.setTransitionCount(state, nextState, score);
						nextStates.add(nextState);
					}
				}
				// System.out.println("States: "+nextStates);
				states = nextStates;
			}
			return trellis;
		}
	}

	/**
	 * States are pairs of tags along with a position index, representing the
	 * two
	 * tags preceding that position. So, the START state, which can be gotten by
	 * State.getStartState() is [START, START, 0]. To build an arbitrary state,
	 * for example [DT, NN, 2], use the static factory method
	 * State.buildState("DT", "NN", 2). There isnt' a single final state, since
	 * sentences lengths vary, so State.getEndState(i) takes a parameter for the
	 * length of the sentence.
	 */
	static class State {

		private static transient Interner<State>	stateInterner	= new Interner<State>(
				state -> new State(state));

		private static transient State				tempState		= new State();

		int											position;

		String										previousPreviousTag;

		String										previousTag;

		private State() {
		}

		private State(State state) {
			setState(state.getPreviousPreviousTag(), state.getPreviousTag(),
					state.getPosition());
		}

		public static State buildState(String previousPreviousTag,
				String previousTag, int position) {
			tempState.setState(previousPreviousTag, previousTag, position);
			return stateInterner.intern(tempState);
		}

		public static State getStartState() {
			return buildState(START_TAG, START_TAG, 0);
		}

		public static State getStopState(int position) {
			return buildState(STOP_TAG, STOP_TAG, position);
		}

		public static List<String> toTagList(List<State> states) {
			final List<String> tags = new ArrayList<String>();
			if (states.size() > 0) {
				tags.add(states.get(0).getPreviousPreviousTag());
				for (final State state : states) {
					tags.add(state.getPreviousTag());
				}
			}
			return tags;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof State)) {
				return false;
			}

			final State state = (State) o;

			if (position != state.position) {
				return false;
			}
			if (previousPreviousTag != null
					? !previousPreviousTag.equals(state.previousPreviousTag)
					: state.previousPreviousTag != null) {
				return false;
			}
			if (previousTag != null ? !previousTag.equals(state.previousTag)
					: state.previousTag != null) {
				return false;
			}

			return true;
		}

		public State getNextState(String tag) {
			return State.buildState(getPreviousTag(), tag, getPosition() + 1);
		}

		public int getPosition() {
			return position;
		}

		public String getPreviousPreviousTag() {
			return previousPreviousTag;
		}

		public State getPreviousState(String tag) {
			return State.buildState(tag, getPreviousPreviousTag(),
					getPosition() - 1);
		}

		public String getPreviousTag() {
			return previousTag;
		}

		@Override
		public int hashCode() {
			int result;
			result = position;
			result = 29 * result
					+ (previousTag != null ? previousTag.hashCode() : 0);
			result = 29 * result + (previousPreviousTag != null
					? previousPreviousTag.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "[" + getPreviousPreviousTag() + ", " + getPreviousTag()
					+ ", " + getPosition() + "]";
		}

		private void setState(String previousPreviousTag, String previousTag,
				int position) {
			this.previousPreviousTag = previousPreviousTag;
			this.previousTag = previousTag;
			this.position = position;
		}
	}

	/**
	 * Tagged sentences are a bundling of a list of words and a list of their
	 * tags.
	 */
	static class TaggedSentence {
		List<String>	tags;
		List<String>	words;

		public TaggedSentence(List<String> words, List<String> tags) {
			this.words = words;
			this.tags = tags;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof TaggedSentence)) {
				return false;
			}

			final TaggedSentence taggedSentence = (TaggedSentence) o;

			if (tags != null ? !tags.equals(taggedSentence.tags)
					: taggedSentence.tags != null) {
				return false;
			}
			if (words != null ? !words.equals(taggedSentence.words)
					: taggedSentence.words != null) {
				return false;
			}

			return true;
		}

		public List<String> getTags() {
			return tags;
		}

		public List<String> getWords() {
			return words;
		}

		@Override
		public int hashCode() {
			int result;
			result = words != null ? words.hashCode() : 0;
			result = 29 * result + (tags != null ? tags.hashCode() : 0);
			return result;
		}

		public int size() {
			return words.size();
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			for (int position = 0; position < words.size(); position++) {
				final String word = words.get(position);
				final String tag = tags.get(position);
				sb.append(word);
				sb.append("_");
				sb.append(tag);
			}
			return sb.toString();
		}
	}

	/**
	 * A Trellis is a graph with a start state an an end state, along with
	 * successor and predecessor functions.
	 */
	static class Trellis<S> {
		CounterMap<S, S>	backwardTransitions;
		S					endState;
		CounterMap<S, S>	forwardTransitions;
		S					startState;

		public Trellis() {
			forwardTransitions = new CounterMap<S, S>();
			backwardTransitions = new CounterMap<S, S>();
		}

		/**
		 * For a given state, returns a counter over what states can precede it
		 * in
		 * the markov process, along with the cost of that transition.
		 */
		public Counter<S> getBackwardTransitions(S state) {
			return backwardTransitions.getCounter(state);
		}

		/**
		 * Get the unique end state for this trellis.
		 */
		public S getEndState() {
			return endState;
		}

		/**
		 * For a given state, returns a counter over what states can be next in
		 * the
		 * markov process, along with the cost of that transition. Caution: a
		 * state
		 * not in the counter is illegal, and should be considered to have cost
		 * Double.NEGATIVE_INFINITY, but Counters score items they don't contain
		 * as
		 * 0.
		 */
		public Counter<S> getForwardTransitions(S state) {
			return forwardTransitions.getCounter(state);

		}

		/**
		 * Get the unique start state for this trellis.
		 */
		public S getStartState() {
			return startState;
		}

		public void setStartState(S startState) {
			this.startState = startState;
		}

		public void setStopState(S endState) {
			this.endState = endState;
		}

		public void setTransitionCount(S start, S end, double count) {
			forwardTransitions.setCount(start, end, count);
			backwardTransitions.setCount(end, start, count);
		}
	}

	/**
	 * A TrellisDecoder takes a Trellis and returns a path through that trellis
	 * in
	 * which the first item is trellis.getStartState(), the last is
	 * trellis.getEndState(), and each pair of states is conntected in the
	 * trellis.
	 */
	static interface TrellisDecoder<S> {
		List<S> getBestPath(Trellis<S> trellis);
	}
}
