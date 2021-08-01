SRC=src
LIB=src:lib/lwjgl.jar:lib/lwjgl_util.jar
NATIVES=lib/natives
BUILD=build

default: $(BUILD)
	@find -name "*.java" > sources.txt
	@javac -d "$(BUILD)" -cp "$(SRC):$(LIB)" @sources.txt
	@rm sources.txt

server:
	@export LD_LIBRARY_PATH="$$LD_LIBRARY_PATH:$(NATIVES)"; java -cp "$(BUILD):$(LIB)" fr.coudert.game.ServerMain
game:
	@export LD_LIBRARY_PATH="$$LD_LIBRARY_PATH:$(NATIVES)"; java -cp "$(BUILD):$(LIB)" fr.coudert.game.GameMain
editor:
	@export LD_LIBRARY_PATH="$$LD_LIBRARY_PATH:$(NATIVES)"; java -cp "$(BUILD):$(LIB)" fr.coudert.editor.EditorMain

$(BUILD):
	mkdir $(BUILD)