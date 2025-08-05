# Deep Image Similarity Matching System

This project implements a deep learning-based system for **visual similarity matching**, with applications in **image retrieval**, **duplicate detection**, and **visual verification**.

### Key techniques:
- CNN-based feature extractors (VGG, ResNet, EfficientNet)
- Triplet loss and Siamese networks for embedding learning
- Cosine similarity for similarity scoring
- Comprehensive evaluation: AUC, MAP@K, PR curves, and t-SNE visualization

> 🚀 Final model achieved **Top-10 ranking on Kaggle leaderboard** using a fine-tuned ResNet152 architecture.

---

## 🧠 Model Overview
- **Backbone:** ResNet152 (pretrained on ImageNet)
- **Input Size:** 128x128
- **Batch Size:** 32
- **Loss Function:** Triplet loss
- **Embedding Similarity:** Cosine distance

---

## ⚙️ Key Components

### 1. 🔧 Data Preprocessing
- Image loading, resizing, and padding to square shape
- Rescaling using Keras `preprocess_input`

### 2. 🧪 Triplet-Based Training
- Triplet samples: (Anchor, Positive, Negative)
- Custom Triplet Loss function with TensorFlow backend
- Training callbacks: EarlyStopping and ModelCheckpoint

### 3. 📊 Evaluation Metrics
- Cosine similarity between embeddings
- AUC (Area Under Curve)
- MAP@K (Mean Average Precision at K)
- Precision-Recall curve
- t-SNE embedding visualization
- Distance distributions

---

## 🔬 Fine-Tuning & Hyperparameter Search

Extensive experiments conducted on:
- Learning rates
- Margin values for triplet loss
- Batch sizes
- Distance metrics (Euclidean, Cosine)

> Best performance logged in `best_model_info.txt`

---

## 🖼️ Visualization
- t-SNE plots of embedding space
- Heatmaps of learned features
- Histogram of pairwise distances
- Precision-Recall curves

---

## 📦 Dependencies

Core libraries:
- `TensorFlow`, `Keras`
- `scikit-learn`, `Pandas`
- `Matplotlib`, `Seaborn`
- `OpenCV` for image processing

---

## 💡 Future Improvements
- Integrate **hard negative mining** for efficient triplet learning
- Experiment with **Vision Transformers (ViT)** as backbone
- Explore **learned similarity metrics** beyond cosine
- Train an **end-to-end Siamese architecture** for retrieval