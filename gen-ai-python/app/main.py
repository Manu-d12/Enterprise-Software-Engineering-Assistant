import os
from dotenv import load_dotenv

load_dotenv()

GROQ_API_KEY = os.getenv("GROQ_API_KEY")

print(GROQ_API_KEY)
print("Hello to World of GenAI")