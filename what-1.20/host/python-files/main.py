import subprocess
import sys

dependencies = [
    'paho.mqtt',
    'pycryptodome',
    'pillow',
    'dearpygui',
]

for dependency in dependencies:
    try:
        subprocess.check_call([sys.executable, '-m', 'pip', 'install', dependency])
    except subprocess.CalledProcessError as e:
        print(f"Failed to install {dependency}. Error: {e}")
