import tensorflow as tf
import numpy as np

def calc_BP(PPG):
    # Load TFLite model and allocate tensors.
    interpreter = tf.lite.Interpreter(model_path="/storage/self/primary/DeepModel/model.tflite")
#     interpreter = tf.lite.Interpreter(model_path="DeepModel/model.tflite")
    interpreter.allocate_tensors()


    # Get input and output tensors.
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    # Test model.
    input_shape = input_details[0]['shape']
    input_data = np.array([[PPG]], dtype=np.float32)
    interpreter.set_tensor(input_details[0]['index'], input_data)

    interpreter.invoke()

    # The function `get_tensor()` returns a copy of the tensor data.
    # Use `tensor()` in order to get a pointer to the tensor.
    output_data = interpreter.get_tensor(output_details[0]['index'])

    prediction = int(output_data[0][0])

    return str(prediction)




