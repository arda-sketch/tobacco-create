from PIL import Image

input_file = "tobacco.png"
output_file = "tobacco_16x16.png"

img = Image.open(input_file).convert("RGBA")

img_16 = img.resize((16, 16), Image.Resampling.NEAREST)

img_16.save(output_file)

print(f"Готово: {output_file}")