# Climate Fact Checker: NLP-based Claim Verification System

This project builds an automated fact-checking system for climate-related claims using a retrieval-augmented classification pipeline. It is designed under restricted academic settings (no pretrained models), and trained from scratch using custom NLP components.

## 🚀 Task Overview
Given a claim (e.g., "CO2 doubling only increases temperature by 1°C"), the system retrieves relevant scientific evidence and classifies the claim as one of:
- `SUPPORTS`
- `REFUTES`
- `NOT_ENOUGH_INFO`
- `DISPUTED`

## 📊 System Architecture
- **Retriever**: TF-IDF + LSA (Truncated SVD) semantic search from large corpus
- **Classifier**: Transformer-based model enhanced with Word2Vec embeddings
- **Training**: Supervised learning from scratch using PyTorch

## 📈 Performance
Achieved **95% accuracy** on the training set with significant improvement over baseline on the development set.

## 🔍 Sample Prediction
**Claim**: "South Australia has the most expensive electricity in the world"  
**Prediction**: SUPPORTS  
**Evidence**: `["evidence-67732", "evidence-572512"]`

## ⚠️ Academic Constraints
- No BERT, HuggingFace, or GPT usage
- No pretrained weights or embeddings
- Must run on Google Colab GPU
- Custom code required (no open-source reuse)

---

## 🧠 Key Features

### Evidence Retrieval (TF-IDF + LSA)
- Vectorize claims and evidences using TF-IDF
- Reduce dimensionality via Truncated SVD (LSA)
- Compute cosine similarity between claims and evidences
- Select top-k passages (e.g., k=3) as candidate evidences

### Word Embedding & Semantic Representation
- Custom Word2Vec embeddings trained on combined corpus
- Max pooling applied to token vectors for fixed-length representation

### Transformer-based Claim Classification
- Implemented lightweight Transformer encoder in PyTorch
- Classifies stance based on [claim; evidence] pairs
- Uses cross-entropy loss; trained from scratch

---

## 📈 Training & Evaluation
- Combined train + dev sets used for final training
- Accuracy tracked and plotted for model validation
- Final predictions formatted per Codalab submission requirements

### 📤 Output Format Example
```json
{
  "claim-123": {
    "claim_label": "REFUTES",
    "evidences": ["evidence-456", "evidence-789"]
  }
}



### 🔧 Further Improvements

While the current system performs well under constrained academic settings, several improvements could further enhance its retrieval and classification capabilities:
	•	Semantic Retrieval with Word2Vec
Replace TF-IDF with Word2Vec-based semantic similarity to better capture meaning beyond surface-level word overlap. This could improve the relevance of retrieved evidence, especially when claims and passages are lexically different.
	•	Post-retrieval Heuristics
Apply lightweight rule-based filters (e.g., keyword overlap, entity matching) after initial retrieval to refine top-k evidences and reduce noise.
	•	Richer Embeddings with FastText
Use FastText embeddings to handle out-of-vocabulary words and morphological variants, particularly useful for scientific terminology in climate-related content.
	•	Alternative Classifier Architecture
Experiment with BiGRU + attention mechanisms as an alternative to the Transformer, potentially improving performance and training stability on smaller datasets.
	•	Hard Negative Mining
Introduce hard negative examples—semantically similar but label-different evidence—to improve the classifier’s robustness and decision boundaries.

These enhancements align with the project’s no-pretrained, from-scratch constraints, and could lead to both higher retrieval accuracy and more reliable claim classification.