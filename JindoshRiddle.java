import java.util.*;

public class JindoshRiddle {
	
	public static class Node {

		public static Set<Node> nodes = new HashSet<Node>();

		public static Node getNode(String type, String value) {
			for (Node node : nodes) {
				if (node.type.equals(type) && node.value.equals(value)) {
					return node;
				}
			}
			return null;
		}

		private String value = null;
		private String type = null;
		private Map<Node, String> links = new HashMap<Node, String>();

		public Node(String value, String type) {
			this.value = value;
			this.type = type;
			nodes.add(this);
			for (Node node : nodes) {
				if (node != this) {
					node.setLink(this, null);
				}
			}
		}

		public String getValue() {
			return this.value;
		}

		public String getType() {
			return this.type;
		}

		public Map<Node, String> getLinks() {
			return this.links;
		}

		public static void traverseAndProcessNegative(Node node, Set<Node> buffer) {

				if (buffer.contains(node)) {
					return;
				}

				buffer.add(node);
				for (Node thisNode : node.links.keySet()) {
					String thisRelationship = node.links.get(thisNode);
					if (thisRelationship != null && thisRelationship.equals("direct")) {
						traverseAndProcessNegative(thisNode, buffer);
					}
				}
		}

		public void setLink(Node node, String relationship) {
			links.put(node, relationship);
			String reverseRelationship = relationship;
			if (relationship != null) {
				if (relationship.equals("left of")) {
					reverseRelationship = "right of";
				} else if (relationship.equals("right of")) {
					reverseRelationship = "left of";
				} 
			}

			if (relationship != null && ((relationship.contains("of") || relationship.equals("next to")) || relationship.equals("not"))) {
				Set<Node> buffer = new HashSet<Node>();
				buffer.add(this);
				buffer.add(node);
				for (Node thisNode : node.links.keySet()) {
					String thisRelationship = node.links.get(thisNode);
					if (thisRelationship != null && thisRelationship.equals("direct")) {
						traverseAndProcessNegative(thisNode, buffer);
					}
				}

				for (Node thisNode: buffer) {
					if (thisNode != this && thisNode != node) {
						links.put(thisNode, "not");
					}
				}
			}

			node.setReverseLink(node, reverseRelationship);

			if (relationship != null && relationship.equals("direct")) {
				String type = node.type;
				String value = node.value;
				for (Node thisNode : nodes) {
					if (thisNode.type.equals(type) && !thisNode.value.equals(value)) {
						thisNode.setLink(this, "not");
					}
				}
			}
		}

		public String getPosition() {
			Set<Node> traversedNodes = new HashSet<Node>();
			return getPosition(this, traversedNodes);
		}

		private static String getPosition(Node node, Set<Node> traversedNodes) {

			if (traversedNodes.contains(node)) {
				return null;
			}

			traversedNodes.add(node);

			if (node.type.equals("position")) {
				return node.value;
			} else {
				for (Node linkedNode : node.links.keySet()) {
					String relationship = node.links.get(linkedNode);

					if (relationship == null || !relationship.equals("direct")) {
						continue;
					}

					String position = getPosition(linkedNode, traversedNodes);
					if (position != null) {
						return position;
					}
				}
			}

			return null;
		}

		private void setReverseLink(Node node, String relationship) {
			links.put(node, relationship);
		}

	}

	static Map<String, String>[] solution = new Map[5];
	static {
		for (int i = 0; i < solution.length; i++) {
			solution[i] = new HashMap<String, String>();
		}
	}

	public static void main(String[] args) {

		Set<Node> nodes = Node.nodes;

		// persons
		Node winslow = new Node("Winslow", "person");
		Node marcolla = new Node("Marcolla", "person");
		Node contee = new Node("Contee", "person");
		Node natsiou = new Node("Natsiou", "person");
		Node finch = new Node("Finch", "person");

		// color of clothing
		Node red = new Node("red", "color");
		Node white = new Node("white", "color");
		Node blue = new Node("blue", "color");
		Node purple = new Node("purple", "color");
		Node green = new Node("green", "color");

		// drinks
		Node wine = new Node("wine", "drink");
		Node whiskey = new Node("whiskey", "drink");
		Node beer = new Node("beer", "drink");
		Node absinthe = new Node("absinthe", "drink");
		Node rum = new Node("rum", "drink");

		// home cities
		Node dabovka = new Node("Dabovka", "home");
		Node baleton = new Node("Baleton", "home");
		Node dunwall = new Node("Dunwall", "home");
		Node karnaca = new Node("Karnaca", "home");
		Node fraeport = new Node("Fraeport", "home");

		// positions
		Node position0 = new Node("0", "position");
		Node position1 = new Node("1", "position");
		Node position2 = new Node("2", "position");
		Node position3 = new Node("3", "position");
		Node position4 = new Node("4", "position");

		// heirlooms
		Node ring = new Node("ring", "heirloom");
		Node diamond = new Node("diamond", "heirloom");
		Node warMedal = new Node("war medal", "heirloom");
		Node snuffTin = new Node("snuff tin", "heirloom");
		Node birdPendant = new Node("bird pendant", "heirloom");

		// Madam Natsiou wore a jaunty red hat
		natsiou.setLink(red, "direct");

		// Baroness Finch was at the far left, next to the guest wearing a purple jacket
		finch.setLink(position0, "direct");
		finch.setLink(purple, "next to");
		
		// The lady in white sat left of someone in blue.
		white.setLink(blue, "left of");

		// I remember that white outfit because the woman spilled her wine all over it
		wine.setLink(white, "direct");

		// The traveler from Dabovka was dressed entirely in green
		dabovka.setLink(green, "direct");

		// When one of the dinner guests bragged about her ring, the woman next to her said they were finer in Dabovka, where she lived
		ring.setLink(dabovka, "next to");

		// So Lady Winslow showed off a prized diamond, at which the lady from Karnaca scoffed, saying it was no match for her snuff tin
		diamond.setLink(winslow, "direct");
		snuffTin.setLink(karnaca, "direct");

		// Someone else carried a valuable war medal, and when she saw it, the vistor from Dunwall next to her almost spilled her neighbor's whiskey
		warMedal.setLink(dunwall, "next to");
		dunwall.setLink(whiskey, "next to");

		// questionable?
		//warMedal.setLink(whiskey, "direct");

		// Countess Contee raised her beer in toast
		contee.setLink(beer, "direct");

		// The lady from Fraeport, full of absinthe, jumped up onto the table, falling onto the guest in the center seat, spilling the poor woman's rum
		fraeport.setLink(absinthe, "direct");
		rum.setLink(position2, "direct");

		// Then Doctor Marcolla captivated them all with a story about her wild youth in Baleton
		baleton.setLink(marcolla, "direct");

		String[] leftRight = { "left", "right"};
		//String[] leftRight = { "left" };


		for (Node node : nodes) {

			for (int i = 0; i < leftRight.length; i++) {

				String direction = leftRight[i];

				Set<Node> traversedNodes = new HashSet<Node>();
				Map<String, String> solutionMap = new HashMap<String, String>();
				/*
				solutionMap.put("position", null);
				solutionMap.put("person", null);
				solutionMap.put("heirloom"., null);
				*/
				traverseGraph(node, traversedNodes, solutionMap, direction);

				String position = solutionMap.get("position");
				String person = solutionMap.get("person");
				String heirloom = solutionMap.get("heirloom");
				String drink = solutionMap.get("drink");
				String color = solutionMap.get("color");
				String home = solutionMap.get("home");

				//if (position != null && person != null && heirloom != null) {
					//System.out.println(node.getLinks().keySet().size());
					System.out.println(position + ", " + person + ", " + heirloom + ", " +
										drink + ", " + color + ", " + home
						);
				//}
			}
		}

	}

	private static void traverseGraph(Node node, Set<Node> traversedNodes, Map<String, String> solutionMap, String direction) {
		if (traversedNodes.contains(node)) {
			return;
		}

		traversedNodes.add(node);
		solutionMap.put(node.getType(), node.getValue());

		Map<Node, String> links = node.getLinks();
		for (Node linkedNode : links.keySet()) {
			String relationship = links.get(linkedNode);
			if (relationship == null) {
				int notCount = 0;
				Map<Node, String> links2 = node.getLinks();
				for (Node thisNode : links2.keySet()) {
					String thisRelationship = links2.get(thisNode);
					if (thisNode.getType().equals(linkedNode.getType()) && !thisNode.getValue().equals(linkedNode.getValue()) 
						&& thisRelationship != null && 
						(thisRelationship.equals("not") || thisRelationship.equals("next to") || thisRelationship.contains("of"))
						) {
						notCount++;
					}
				}
				if (notCount == 4) {
					traverseGraph(linkedNode, traversedNodes, solutionMap, direction);
				}
			}
			else if (relationship.equals("direct")) {
				traverseGraph(linkedNode, traversedNodes, solutionMap, direction);
			} else if (relationship.equals("left of") || (relationship.equals("next to") && direction.equals("left")) ) {
				String positionString = linkedNode.getPosition();
				if (positionString != null) {
					int position = Integer.parseInt(positionString);
					int leftOfPosition = position + 1;
					if (leftOfPosition <= 4) {
						Node leftOfNode = Node.getNode("position", Integer.toString(leftOfPosition));
						traverseGraph(leftOfNode, traversedNodes, solutionMap, direction);
					}
				}
			} else if (relationship.equals("right of") || (relationship.equals("next to") && direction.equals("right"))) {
				String positionString = linkedNode.getPosition();
				if (positionString != null) {
					int position = Integer.parseInt(positionString);
					int rightOfPosition = position - 1;
					if (rightOfPosition >= 0) {
						Node rightOfNode = Node.getNode("position", Integer.toString(rightOfPosition));
						traverseGraph(rightOfNode, traversedNodes, solutionMap, direction);
					}
				}
			} 
		}
	}

}